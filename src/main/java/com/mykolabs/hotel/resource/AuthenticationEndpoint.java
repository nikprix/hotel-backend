package com.mykolabs.hotel.resource;

import com.mykolabs.hotel.exceptions.GenericErrorMessage;
import com.mykolabs.hotel.authentication.PassGen;
import com.mykolabs.hotel.authentication.Token;
import com.mykolabs.hotel.beans.Employee;
import com.mykolabs.hotel.exceptions.AuthorizationException;
import com.mykolabs.hotel.persistence.EmployeeDAO;
import com.mykolabs.hotel.util.TokenUtil;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 *
 * @author nikprixmar
 */
@Path("/authentication")
public class AuthenticationEndpoint {

    private static final Logger log = Logger.getLogger(AuthenticationEndpoint.class.getName());
    
    private static final int EXPIRATION = 3600;

    /**
     * HK2 Injection.
     */
    @Context
    SecretKey key;

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateEmployee(Employee employee) throws Exception {

        // extracting username/password from request's body
        String username = employee.getUsername();
        String password = employee.getPassword();

        log.log(Level.INFO, "Username: {0}", username);
        log.log(Level.INFO, "Password: {0}", password);

        // Authenticate the user using the credentials provided and retrieve authenticated user obj
        Employee authenticatedUser = authenticateEmployee(username, password);

        // Issue a token for the user
        Date expiry = getExpiryDate(EXPIRATION);

        // The issued token must be associated to a user
        // Return the issued token
        String jwtString = TokenUtil.getJWTString(authenticatedUser.getUsername(),
                authenticatedUser.getEmployeeId(), authenticatedUser.getRoles().split(","), 
                expiry, key);

        Token token = new com.mykolabs.hotel.authentication.Token();

        token.setAuthToken(jwtString);
        token.setExpires(expiry);

        return Response
                .status(Response.Status.OK)
                .type("application/json")
                .entity(token)
                .build();
    }

    /**
     * Provides authentication of the user against DB stored password using
     * provided in the request details.
     *
     * @param username
     * @param password
     * @throws Exception
     */
    private Employee authenticateEmployee(String username, String password) throws Exception {

        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee empoyee;
        
        // retrieve user's detaiuls from the DB
        try{
         empoyee = employeeDAO.getEmployee(username);
         log.log(Level.INFO, "Retrieved employee id: {0}", empoyee.getEmployeeId());
        } catch (Exception ex){
             // validating if valid username was provided
             ex.printStackTrace();
            throw new AuthorizationException("Invalid username provided!");
        }
        
        // validating if valid username was provided
        if(empoyee.getEmployeeId() == null){
            throw new AuthorizationException("Invalid username provided!");
        }
         
        // validating provided password againt stored value and throwing exception if they don't match
        if (!PassGen.check(password, empoyee.getPassword())) {
            throw new AuthorizationException("Invalid password provided!");
        }
        return empoyee;
    }

    /**
     * get Expire date in minutes.
     *
     * @param minutes the minutes in the future.
     * @return
     */
    private Date getExpiryDate(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

}
