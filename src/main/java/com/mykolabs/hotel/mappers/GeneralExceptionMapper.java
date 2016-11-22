package com.mykolabs.hotel.mappers;

import com.mykolabs.hotel.exceptions.GenericErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper class to 
 * @author nikprixmar
 */
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        GenericErrorMessage e = new GenericErrorMessage();
        e.setMessage(exception.getMessage());
        e.setCode(Status.UNAUTHORIZED.getStatusCode());

        return Response
                .status(Status.UNAUTHORIZED)
                .type("application/json")
                .entity(e)
                .build();

    }

}
