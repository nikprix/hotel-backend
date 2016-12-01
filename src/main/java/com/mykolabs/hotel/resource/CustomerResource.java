package com.mykolabs.hotel.resource;

import com.mykolabs.hotel.authentication.Secured;
import com.mykolabs.hotel.util.Validator;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;

import com.mykolabs.hotel.authentication.AuthenticationFilter;
import com.mykolabs.hotel.beans.Customer;
import com.mykolabs.hotel.mappers.AuthenticationExceptionMapper;
import com.mykolabs.hotel.mappers.GeneralExceptionMapper;
import com.mykolabs.hotel.persistence.CustomerDAO;

/**
 * REST Web Service to manage hotel Customers (Jersey / ).
 *
 * ref: http://howtodoinjava.com/jersey/jersey-restful-client-examples/
 *
 * Glassfish fix: https://java.net/jira/browse/GLASSFISH-21440
 *
 * @author nikprixmar
 */
@Path("customers")
public class CustomerResource extends ResourceConfig {

    /**
     * Creates a new instance of CustomerResource
     */
    public CustomerResource() {
        
                // ============= IMPORTANT =============== //
        // registering resources and providers is DONE in web.xml
        // registering in both places, here and in web.xml will end up
        // having really bad exception while deploying app:
        // "Caused by: java.lang.OutOfMemoryError: GC overhead limit exceeded"
        // AVOID!!!

        /* REGISTERING Resources and Providers */
        // also, init-params needs to be added to the web.xml file
        //registering using ResourceConfig (this class extends ResourceConfig) 
        
//        packages("com.mykolabs.hotel.authentication;com.mykolabs.hotel.mappers;");
//        register(AuthenticationFilter.class);
//        register(AuthenticationExceptionMapper.class);
//        register(GeneralExceptionMapper.class);
    }

    /**
     * Retrieves single customer from the DB by provided ID.
     *
     * @param id
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("id") int id) throws SQLException {

        if (id < 0) {
            return Response.noContent().build();
        }

        CustomerDAO customerDAO = new CustomerDAO();
        // retrieving customer from the DB
        Customer singleCustomerFromDB = customerDAO.getCustomer(id);

        GenericEntity<Customer> entity = new GenericEntity<>(singleCustomerFromDB, Customer.class);

        return Response.ok().entity(entity).build();
    }

    /**
     * PUT method for updating an instance of Customer
     *
     * @param customer
     * @param room
     * @return
     * @throws java.sql.SQLException
     */
    @PUT
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response editCustomer(final Customer customer) throws SQLException {

        if (!Validator.isCustomerExists(customer)) {
            return Response.status(400).entity("{\"error\": \"Please add customer details!\"}").build();
        }

        if (!Validator.isCustomerValid(customer)) {
            return Response.status(400).entity("{\"error\": \"Some customer details are missing!\"}").build();
        }

        CustomerDAO customerDAO = new CustomerDAO();

        int customerStatus = 0;

        customerStatus = customerDAO.updateCustomer(customer);

        if (customerStatus != 1) {
            return Response.status(400).entity("{\"error\":\"An error occured while updating customer details. Please try again\"}").build();
        }

        return Response.ok().entity(customer).build();
    }

    /**
     * POST method for creating an instance of Customer
     *
     * @param customer
     * @return
     * @throws java.net.URISyntaxException
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response addCustomer(final Customer customer) throws URISyntaxException, SQLException, Exception {

        if (!Validator.isCustomerExists(customer)) {
            return Response.status(400).entity("{\"error\": \"Please add customer details!\"}").build();
        }

        if (!Validator.isCustomerValid(customer)) {
            return Response.status(400).entity("{\"error\": \"Some customer details are missing!\"}").build();
        }

        CustomerDAO customerDAO = new CustomerDAO();

        int customerId;

        customerId = customerDAO.addCustomer(customer);

        if (customerId <= 0) {
            return Response.status(400).entity("{\"error\":\"An error occured while adding a customer. Please try again\"}").build();
        }

        return Response
                .created(new URI("customers/" + customerId))
                .entity("{\"customerId\":"+customerId+"}")
                .build();
    }

    @OPTIONS
    public String getOptions() {
        return "";
    }
}
