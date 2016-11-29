package com.mykolabs.hotel.resource;

import com.mykolabs.hotel.authentication.Secured;
import com.mykolabs.hotel.beans.Reservation;
import com.mykolabs.hotel.beansLists.ReservationList;
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
import com.mykolabs.hotel.beansLists.AllReservationListWithCustomer;
import com.mykolabs.hotel.beans.ReservationSearch;
import com.mykolabs.hotel.beans.TodayDate;
import com.mykolabs.hotel.beansLists.TodayReservationList;
import com.mykolabs.hotel.mappers.AuthenticationExceptionMapper;
import com.mykolabs.hotel.mappers.GeneralExceptionMapper;
import java.util.Date;
import javax.ws.rs.DELETE;

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
     * Retrieves ALL reservations from the DB. Future enhancement - add limits
     * by DateOfEntry - start / end date.
     *
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public AllReservationListWithCustomer getAllReservations() throws SQLException {

        // NOTE, using 'TodayReservationList' class here, since it has 
        // all needed data, but 
        AllReservationListWithCustomer allReservationList = new AllReservationListWithCustomer();
        ReservationDAO reservationDAO = new ReservationDAO();
        // retrieving reservations from the DB
        allReservationList.setAllReservationList(reservationDAO.getAllReservationsWithCustomerData());

        return allReservationList;
    }

    /**
     * Retrieves ALL TODAY reservations from the DB.
     *
     * @param currentDate
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todayReservations")
    public TodayReservationList getAllTodayReservations(TodayDate currentDate) throws SQLException {

        TodayReservationList todayReservationList = new TodayReservationList();
        ReservationDAO reservationDAO = new ReservationDAO();
        // retrieving all today reservations from the DB
        todayReservationList.setTodayReservationList(reservationDAO.getAllTodayReservations(currentDate));

        return todayReservationList;
    }

    /**
     * Retrieves ALL rooms from the DB which match provided search criteria.
     * Have to use 'TodayReservationList' but indeed it returns not only today
     * data, but data, which matches search criteria
     *
     * @param reservationSearch
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/availableReservations")
    public TodayReservationList getAllavailableReservations(final ReservationSearch reservationSearch) throws SQLException {

        TodayReservationList reservationList = new TodayReservationList();
        ReservationDAO reservationDAO = new ReservationDAO();
        // retrieving rooms from the DB
        reservationList.setTodayReservationList(reservationDAO.getReservationsForCheckin(reservationSearch));

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

    /**
     * Removes reservation by provided id. NOTE, since cascading was enabled for
     * these tables, it will also remove related payment(s), which holds
     * reservation id. Not good idea, but works for demo purposes.
     *
     * @param id
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @DELETE
    @Secured
    @Path("{id}")
    public Response deleteReservation(@PathParam("id") int id) throws SQLException {

        ReservationDAO reservationDAO = new ReservationDAO();
        int deleteStatus = reservationDAO.deleteReservation(id);

        if (deleteStatus != 1) {
            return Response.status(400).entity("{\"error\":\"An error occured while removing reservation. "
                    + "Please try again\"}").build();
        } else {
            return Response.status(202).entity("{\"message\":\"Reservation and payment info were removed\"}").build();
        }
    }

    @OPTIONS
    public String getOptions() {
        return "";
    }
}
