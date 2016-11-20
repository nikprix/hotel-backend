package com.mykolabs.hotel.authentication;

import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.exceptions.AuthorizationException;
import com.mykolabs.hotel.persistence.EmployeeDAO;
import com.mykolabs.hotel.resource.AuthenticationEndpoint;
import com.mykolabs.hotel.util.TokenUtil;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.crypto.SecretKey;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * Filter class, which implements ContainerRequestFilter, allowing to handle the
 * request. The defined name-binding annotation @Secured will be used to
 * decorate a filter class, which implements ContainerRequestFilter, allowing
 * you to handle the request.
 *
 * @author nikprixmar
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(AuthenticationFilter.class.getName());

    /**
     * HK2 Injection.
     */
    @Context
    SecretKey key;

    /**
     * The ContainerRequestContext helps you to extract the token from the HTTP
     * request.
     *
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.log(Level.INFO, "Entered Filter method");

        String method = requestContext.getMethod().toLowerCase();
        String path = ((ContainerRequest) requestContext).getPath(true).toLowerCase();

        // Get the HTTP Authorization header from the request
        String authorizationHeader
                = ((ContainerRequest) requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        String strToken = extractJwtTokenFromAuthHeader(authorizationHeader);
        try {
            validateToken(requestContext, strToken);
        } catch (Exception ex) {
            log.log(Level.INFO, ex.getMessage());
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    /**
     * Extract token from AUTH header.
     *
     * @param auth
     * @return
     */
    public static String extractJwtTokenFromAuthHeader(String auth) {
        //Replacing "Bearer Token" to "Token" directly
        return auth.replaceFirst("[B|b][E|e][A|a][R|r][E|e][R|r] ", "").replace(" ", "");
    }

    /**
     * Validates retrieved from the headers token against DB value. Issues
     * exception if tokens don't match.
     *
     * @param token
     * @param username
     * @throws Exception
     */
    private void validateToken(ContainerRequestContext requestContext, String token) throws Exception {

        if (TokenUtil.isValid(token, key)) {

            String username = TokenUtil.getName(token, key);
            String[] roles = TokenUtil.getRoles(token, key);
            int id = TokenUtil.getId(token, key);
            Date expirationDate = TokenUtil.getExpiryDate(token, key);
            
            log.log(Level.INFO, "Current date: {0}", TokenUtil.getCurrentDate());
            log.log(Level.INFO, "Token expiry date: {0}", expirationDate);

            if (TokenUtil.getCurrentDate().compareTo(expirationDate) < 0) {
                // token is not expired

                if (username != null && roles.length != 0 && id != -1) {
                    Employee employee = null;
                    try {
                        // retrieving user's profile from the DB
                        EmployeeDAO userDAO = new EmployeeDAO();
                        user = userDAO.getUser(username);
                        // retrieving binded to the user token
                        String retrievedToken = user.getToken();
                        log.log(Level.INFO, "Extracted from DB: {0} : {1}", new Object[]{user.getUsername(), retrievedToken});
                    } catch (EntityNotFoundException e) {
                        throw new com.mykolabs.hotel.exceptions.EntityNotFoundException("User not found " + username);
                    }
                    if (user != null) {
                        if (user.getId() == id && user.getRoles() != null
                                && Arrays.asList(user.getRoles()).containsAll(Arrays.asList(roles))) {
                            // Token is valid
                            log.log(Level.INFO, "Authentication info is valid");
                            return;
                        } else {
                            log.log(Level.INFO, "User id or roles did not match the token");
                        }
                    } else {
                        log.log(Level.INFO, "User not found");
                    }
                } else {
                    log.log(Level.INFO, "name, roles or version missing from token");
                }
            } else {
                throw new AuthorizationException("token is expired");
            }
        } else {
            log.log(Level.INFO, "token is invalid");
        }
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
}
