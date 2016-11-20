package com.mykolabs.hotel.resource;

import com.mykolabs.authentication.AuthenticationFilter;
import com.mykolabs.mappers.AuthenticationExceptionMapper;
import com.mykolabs.mappers.EntityNotFoundMapper;
import com.mykolabs.mappers.GeneralExceptionMapper;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ws.rs.core.Application;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * App configuration class. Used for resources registration and defining
 * Application path.
 *
 * @author nikprixmar
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends ResourceConfig {

    private static final Logger log = Logger.getLogger(ApplicationConfig.class.getName());

    private static SecretKey key;

    public ApplicationConfig() {

        /* REGISTERING Resources and Providers */
        // also, init-params needs to be added to the web.xml file
        //registering using ResourceConfig (this class extends ResourceConfig) 
        packages("com.mykolabs.authentication;com.mykolabs.mappers;com.mykolabs.resource;");
        
        register(AuthenticationFilter.class);
        register(AuthenticationExceptionMapper.class);
        register(GeneralExceptionMapper.class);
        register(EntityNotFoundMapper.class);
        register(AuthenticationEndpoint.class);
        register(AuthenticationEndpoint.class);
        register(BooksResource.class);
        register(CORS.class);

        // turn on Jackson
        register(JacksonFeature.class);

        // defining an AbstractBinder and register it in your JAX-RS application.
        // good read http://stackoverflow.com/a/17133081
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(generateKey()).to(SecretKey.class);
            }
        });
    }

    /**
     * Generates secret KEY for later injection using hk2.
     *
     * @return
     */
    private static SecretKey generateKey() {
        try {
            key = KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // get base64 encoded version of the key if needed
        // String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        log.log(Level.INFO, "Secret KEY generated on start: {0}", Base64.getEncoder().encodeToString(key.getEncoded()));

        return key;
    }

    /*Getters and Setters for the secret key*/
    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }
}
