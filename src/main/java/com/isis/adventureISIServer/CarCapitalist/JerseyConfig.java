package com.isis.adventureISIServer.CarCapitalist;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/adventureisis")
public class JerseyConfig extends ResourceConfig{

        public JerseyConfig() {

            register(Webservices.class);
            register(CORSResponseFilter.class);

        }
    
}
