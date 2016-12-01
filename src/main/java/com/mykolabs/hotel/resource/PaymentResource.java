package com.mykolabs.hotel.resource;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.mykolabs.hotel.authentication.Secured;
import com.mykolabs.hotel.util.Validator;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;

import com.mykolabs.hotel.authentication.AuthenticationFilter;
import com.mykolabs.hotel.beans.Payment;
import com.mykolabs.hotel.mappers.AuthenticationExceptionMapper;
import com.mykolabs.hotel.mappers.GeneralExceptionMapper;
import com.mykolabs.hotel.persistence.PaymentDAO;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;

/**
 * REST Web Service to manage hotel payments (Jersey / ).
 *
 * ref: http://howtodoinjava.com/jersey/jersey-restful-client-examples/
 *
 * Glassfish fix: https://java.net/jira/browse/GLASSFISH-21440
 *
 * @author nikprixmar
 */
@Path("payments")
public class PaymentResource extends ResourceConfig {

    /**
     * Creates a new instance of PaymentResource
     */
    public PaymentResource() {

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
     * Retrieves single payment from the DB by provided ID.
     *
     * @param reservationId
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    @GET
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayment(@PathParam("id") int reservationId) throws SQLException, JsonProcessingException {

        if (reservationId < 0) {
            return Response.noContent().build();
        }

        PaymentDAO paymentDAO = new PaymentDAO();
        // retrieving payment from the DB
        Payment singlePaymentFromDB = paymentDAO.getPaymentByReservationId(reservationId);

        // Filtering out all Payment properties except 'paymentAmount'
        ObjectMapper mapper = new ObjectMapper();
        // first, construct filter provider to exclude all properties but 'cardNumber', bind it as 'paymentFilter'
        FilterProvider filters = new SimpleFilterProvider().addFilter("paymentFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("paymentAmount"));
        // and then serialize using that filter provider:
        mapper.setFilters(filters);
        String json = mapper.writeValueAsString(singlePaymentFromDB);

        // No need to use this code anymore, since it returns ALL payment details
        //GenericEntity<Payment> entity = new GenericEntity<>(singlePaymentFromDB, Payment.class);

        return Response.ok().entity(json).build();
    }

    /**
     * POST method for creating an instance of Payment
     *
     * @param payment
     * @return
     * @throws java.net.URISyntaxException
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response addPayment(final Payment payment) throws URISyntaxException, SQLException, Exception {

        if (!Validator.isPaymentExists(payment)) {
            return Response.status(400).entity("{\"error\": \"Please add payment details!\"}").build();
        }

        if (!Validator.isPaymentValid(payment)) {
            return Response.status(400).entity("{\"error\": \"Some payment details are missing!\"}").build();
        }

        PaymentDAO paymentDAO = new PaymentDAO();

        int paymentId = 0;

        paymentId = paymentDAO.addPayment(payment);

        if (paymentId <= 0) {
            return Response.status(400).entity("{\"error\":\"An error occured while adding a payment. Please try again\"}").build();
        }

               return Response
                .created(new URI("payments/" + paymentId))
                .entity("{\"paymentId\":"+paymentId+"}")
                .build();
    }

    @OPTIONS
    public String getOptions() {
        return "";
    }
}
