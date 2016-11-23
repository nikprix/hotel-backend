package com.mykolabs.hotel.resource;

import com.mykolabs.hotel.authentication.Secured;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beans.ReservationList;
import com.mykolabs.hotel.persistence.ReservationDAO;
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

import com.google.common.collect.Sets;
import com.mykolabs.hotel.authentication.AuthenticationFilter;
import com.mykolabs.hotel.mappers.AuthenticationExceptionMapper;
import com.mykolabs.hotel.mappers.GeneralExceptionMapper;

/**
 * REST Web Service to manage hotel reservations (Jersey / ).
 *
 * ref: http://howtodoinjava.com/jersey/jersey-restful-client-examples/
 *
 * Glassfish fix: https://java.net/jira/browse/GLASSFISH-21440
 *
 * @author nikprixmar
 */
@Path("reservations")
public class ReservationsResource extends ResourceConfig {

    /**
     * Creates a new instance of BooksResource
     */
    public ReservationsResource() {

        /* REGISTERING Resources and Providers */
        // also, init-params needs to be added to the web.xml file
        //registering using ResourceConfig (this class extends ResourceConfig) 
        packages("com.mykolabs.hotel.authentication;com.mykolabs.hotel.mappers;");
        register(AuthenticationFilter.class);
        register(AuthenticationExceptionMapper.class);
        register(GeneralExceptionMapper.class);
    }

    /**
     * Retrieves ALL reservations from the DB. Future enhancement - add limits by
     * DateOfEntry - start / end date.
     *
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public ReservationList getAllReservations() throws SQLException {

        ReservationList reservationList = new ReservationList();
        ReservationDAO reservationDAO = new ReservationDAO();
        // retrieving reservations from the DB
        reservationList.setReservationList(reservationDAO.getAllReservations(0, 100, true));

        return reservationList;
    }

    /**
     * Retrieves single reservation from the DB. 
     *
     * @param id
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReservation(@PathParam("id") int id) throws SQLException {

        if (id < 0) {
            return Response.noContent().build();
        }

        ReservationDAO reservationDAO = new ReservationDAO();
        // retrieving reservation from the DB
        Reservation singleReservationFromDB = reservationDAO.getReservation(id);

        GenericEntity<Reservation> entity = new GenericEntity<>(singleReservationFromDB, Reservation.class);

        return Response.ok().entity(entity).build();
    }

    /**
     * PUT method for updating an instance of ReservationsResource
     *
     * @param reservation
     * @return
     * @throws java.sql.SQLException
     * @throws java.net.URISyntaxException
     */
    @PUT
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response editReservation(final Reservation reservation) throws SQLException {

        if (!Validator.isReservationExists(reservation)) {
            return Response.status(400).entity("{\"error\": \"Please add reservation details!\"}").build();
        }

        if (!Validator.isReservationValid(reservation)) {
            return Response.status(400).entity("{\"error\": \"Some reservation details are missing!\"}").build();
        }

        ReservationDAO reservationDAO = new ReservationDAO();

        int reservationStatus = 0;

        reservationStatus = reservationDAO.updateReservation(reservation);

        if (reservationStatus != 1) {
            return Response.status(400).entity("{\"error\":\"An error occured while updating reservation. Please try again\"}").build();
        }

        return Response.ok().entity(reservation).build();
    }

    /**
     * POST method for creating an instance of ReservationsResource
     *
     * @param reservation
     * @return
     * @throws java.net.URISyntaxException
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response addReservation(final Reservation reservation) throws URISyntaxException, SQLException, Exception {

        if (!Validator.isReservationExists(reservation)) {
            return Response.status(400).entity("{\"error\": \"Please add reservations details!\"}").build();
        }

        if (!Validator.isReservationValid(reservation)) {
            return Response.status(400).entity("{\"error\": \"Some reservation details are missing!\"}").build();
        }

        ReservationDAO reservationDAO = new ReservationDAO();

        int reservationStatus = 0;

        reservationStatus = reservationDAO.addReservation(reservation);

        if (reservationStatus != 1) {
            return Response.status(400).entity("{\"error\":\"An error occured while adding reservation. Please try again\"}").build();
        }

        return Response.created(new URI("reservations/" + reservation.getReservationId())).build();
    }

    @OPTIONS
    public String getOptions() {
        return "";
    }
}
