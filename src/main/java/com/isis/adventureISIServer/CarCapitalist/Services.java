package com.isis.adventureISIServer.CarCapitalist;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Services {
    
    World readWorldFromXml(String username) throws JAXBException {
        JAXBContext cont = JAXBContext.newInstance(World.class);
        Unmarshaller u = cont.createUnmarshaller();
        World world;
        if(new File(username + "-world.xml").exists()) {
            world = (World) u.unmarshal(new File(username + "-world.xml"));
        } else {
            InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
            world = (World) u.unmarshal(new File("world.xml"));
        }   
        saveWorldToXml(world, username);
        return world;
    }
    
    void saveWorldToXml(World world, String username) {
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();
            OutputStream output = new FileOutputStream(username + "-world.xml");  
            m.marshal(world, output);
        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public Boolean updateProduct(String username, ProductType newproduct) throws FileNotFoundException, JAXBException {

        World world = readWorldFromXml(username); 
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) {
            return false;
        }
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            world.setMoney(world.getMoney() - product.getCout() * (1 - Math.pow(product.getCroissance(), newproduct.getQuantite() + 1)) / (1 - product.getCroissance()));
            product.setQuantite(product.getQuantite() + newproduct.getQuantite());
        } else {
            product.setTimeleft(product.getVitesse());
        }
        saveWorldToXml(world, username);
        return true;
    }
 
    public Boolean updateManager(String username, PallierType newmanager) throws FileNotFoundException, JAXBException {

        World world = readWorldFromXml(username);
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        manager.setUnlocked(true);
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        product.setManagerUnlocked(true);
        world.setMoney(world.getMoney() - manager.getSeuil());
        world.setLastupdate(System.currentTimeMillis());
        saveWorldToXml(world, username);
        return true;
    }

    private ProductType findProductById(World world, int id) {
        List<ProductType> products = world.getProducts().getProduct();
        for (ProductType product : products) {
            if (id == product.getId())
                return product;
        }
        return null;
    }

    private PallierType findManagerByName(World world, String name) {
        List<PallierType> managers = world.getManagers().getPallier();
        for (PallierType manager : managers) {
            if (name.equals(manager.getName()))
                return manager;
        }
        return null;
    }
}
