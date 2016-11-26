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

import com.mykolabs.hotel.authentication.AuthenticationFilter;
import com.mykolabs.hotel.beans.Room;
import com.mykolabs.hotel.beans.RoomList;
import com.mykolabs.hotel.beans.RoomSearch;
import com.mykolabs.hotel.mappers.AuthenticationExceptionMapper;
import com.mykolabs.hotel.mappers.GeneralExceptionMapper;
import com.mykolabs.hotel.persistence.RoomDAO;

/**
 * REST Web Service to manage hotel reservations (Jersey / ).
 *
 * ref: http://howtodoinjava.com/jersey/jersey-restful-client-examples/
 *
 * Glassfish fix: https://java.net/jira/browse/GLASSFISH-21440
 *
 * @author nikprixmar
 */
@Path("rooms")
public class RoomResource extends ResourceConfig {

    /**
     * Creates a new instance of BooksResource
     */
    public RoomResource() {

        /* REGISTERING Resources and Providers */
        // also, init-params needs to be added to the web.xml file
        //registering using ResourceConfig (this class extends ResourceConfig) 
        packages("com.mykolabs.hotel.authentication;com.mykolabs.hotel.mappers;");
        register(AuthenticationFilter.class);
        register(AuthenticationExceptionMapper.class);
        register(GeneralExceptionMapper.class);
    }

    /**
     * Retrieves ALL rooms from the DB. Future enhancement - add limits by
     * DateOfEntry - start / end date.
     *
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public RoomList getAllRooms() throws SQLException {

        RoomList roomList = new RoomList();
        RoomDAO roomDAO = new RoomDAO();
        // retrieving rooms from the DB
        roomList.setRoomList(roomDAO.getAllRooms(0, 100, true));

        return roomList;
    }

    /**
     * Retrieves ALL rooms from the DB which match provided search criteria.
     *
     * @param roomSearch
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/availablerooms")
    public RoomList getAllavailableRooms(final RoomSearch roomSearch) throws SQLException {

        RoomList roomList = new RoomList();
        RoomDAO roomDAO = new RoomDAO();
        // retrieving rooms from the DB
        roomList.setRoomList(roomDAO.getAllAvailableRooms(roomSearch));

        return roomList;
    }

    /**
     * Retrieves single room from the DB by provided ID.
     *
     * @param id
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("id") int id) throws SQLException {

        if (id < 0) {
            return Response.noContent().build();
        }

        RoomDAO roomDAO = new RoomDAO();
        // retrieving room from the DB
        Room singleRoomFromDB = roomDAO.getRoom(id);

        GenericEntity<Room> entity = new GenericEntity<>(singleRoomFromDB, Room.class);

        return Response.ok().entity(entity).build();
    }

    /**
     * PUT method for updating an instance of Room
     *
     * @param room
     * @return
     * @throws java.sql.SQLException
     */
    @PUT
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response editRoom(final Room room) throws SQLException {

        if (!Validator.isRoomExists(room)) {
            return Response.status(400).entity("{\"error\": \"Please add room details!\"}").build();
        }

        if (!Validator.isRoomValid(room)) {
            return Response.status(400).entity("{\"error\": \"Some room details are missing!\"}").build();
        }

        RoomDAO roomDAO = new RoomDAO();

        int roomStatus = 0;

        roomStatus = roomDAO.updateRoom(room);

        if (roomStatus != 1) {
            return Response.status(400).entity("{\"error\":\"An error occured while updating room details. Please try again\"}").build();
        }

        return Response.ok().entity(room).build();
    }

    /**
     * POST method for creating an instance of Room
     *
     * @param room
     * @return
     * @throws java.net.URISyntaxException
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response addRoom(final Room room) throws URISyntaxException, SQLException, Exception {

        if (!Validator.isRoomExists(room)) {
            return Response.status(400).entity("{\"error\": \"Please add room details!\"}").build();
        }

        if (!Validator.isRoomValid(room)) {
            return Response.status(400).entity("{\"error\": \"Some room details are missing!\"}").build();
        }

        RoomDAO roomDAO = new RoomDAO();

        int roomStatus = 0;

        roomStatus = roomDAO.addRoom(room);

        if (roomStatus != 1) {
            return Response.status(400).entity("{\"error\":\"An error occured while adding a room. Please try again\"}").build();
        }

        return Response.created(new URI("rooms/" + room.getRoomNumber())).build();
    }

    @OPTIONS
    public String getOptions() {
        return "";
    }
}
