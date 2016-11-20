package com.mykolabs.hotel.resource;

import com.mykolabs.authentication.Secured;
import com.mykolabs.beans.Inventory;
import com.mykolabs.beans.InventoryList;
import com.mykolabs.persistence.BooksDAO;
import com.mykolabs.utils.Validator;
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
import com.mykolabs.authentication.AuthenticationFilter;
import com.mykolabs.mappers.AuthenticationExceptionMapper;
import com.mykolabs.mappers.GeneralExceptionMapper;

/**
 * REST Web Service to manage inventory of books (Jersey / ).
 *
 * ref: http://howtodoinjava.com/jersey/jersey-restful-client-examples/
 *
 * Glassfish fix: https://java.net/jira/browse/GLASSFISH-21440
 *
 * @author nikprixmar
 */
@Path("books")
public class BooksResource extends ResourceConfig {

    /**
     * Creates a new instance of BooksResource
     */
    public BooksResource() {

        /* REGISTERING Resources and Providers */
        // also, init-params needs to be added to the web.xml file
        //registering using ResourceConfig (this class extends ResourceConfig) 
        packages("com.mykolabs.authentication;com.mykolabs.mappers;");
        register(AuthenticationFilter.class);
        register(AuthenticationExceptionMapper.class);
        register(GeneralExceptionMapper.class);
    }

    /**
     * Retrieves ALL books from the DB. Future enhancement - add limits by
     * DateOfEntry - start / end date.
     *
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public InventoryList getAllBooks() throws SQLException {

        InventoryList bookList = new InventoryList();
        BooksDAO booksDAO = new BooksDAO();
        // retrieving books from the DB
        bookList.setInventoryList(booksDAO.getAllBooks(0, 100, true));

        return bookList;
    }

    /**
     * Retrieves single book from the DB. Future enhancement - add limits by
     * DateOfEntry - start / end date.
     *
     * @param isbn
     * @return an instance of java.lang.String
     * @throws java.sql.SQLException
     */
    @GET
    @Secured
    @Path("{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("isbn") String isbn) throws SQLException {

        if (Long.valueOf(isbn) < 0) {
            return Response.noContent().build();
        }

        BooksDAO booksDAO = new BooksDAO();
        // retrieving book from the DB
        Inventory singleBookFromDB = booksDAO.getBook(isbn);

        GenericEntity<Inventory> entity = new GenericEntity<>(singleBookFromDB, Inventory.class);

        return Response.ok().entity(entity).build();
    }

    /**
     * PUT method for updating an instance of BooksResource
     *
     * @param book
     * @return
     */
    @PUT
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response editBook(final Inventory book) throws SQLException, URISyntaxException {

        if (!Validator.isBookExists(book)) {
            return Response.status(400).entity("{\"error\": \"Please add books details!\"}").build();
        }

        if (!Validator.isBookValid(book)) {
            return Response.status(400).entity("{\"error\": \"Some book details are missing!\"}").build();
        }

        BooksDAO booksDAO = new BooksDAO();

        int bookStatus = 0;

        bookStatus = booksDAO.updateBook(book);

        if (bookStatus != 1) {
            return Response.status(400).entity("An error occured while adding book. Please try again").build();
        }

        return Response.ok().entity(book).build();
    }

    /**
     * POST method for creating an instance of BooksResource
     *
     * @param book
     * @param content representation for the resource
     * @return
     * @throws java.net.URISyntaxException
     * @throws java.sql.SQLException
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response addBook(final Inventory book) throws URISyntaxException, SQLException, Exception {

        if (!Validator.isBookExists(book)) {
            return Response.status(400).entity("{\"error\": \"Please add books details!\"}").build();
        }

        if (!Validator.isBookValid(book)) {
            return Response.status(400).entity("{\"error\": \"Some book details are missing!\"}").build();
        }

        BooksDAO booksDAO = new BooksDAO();

        int bookStatus = 0;

        bookStatus = booksDAO.addBook(book);

        if (bookStatus != 1) {
            return Response.status(400).entity("An error occured while adding book. Please try again").build();
        }

        return Response.created(new URI("books/" + book.getIsbn())).build();
    }

    @OPTIONS
    public String getOptions() {
        return "";
    }
}
