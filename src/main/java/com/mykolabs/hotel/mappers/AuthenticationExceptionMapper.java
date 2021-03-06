package com.mykolabs.hotel.mappers;

import com.mykolabs.hotel.exceptions.AuthorizationException;
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
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthorizationException> {

    @Override
    public Response toResponse(AuthorizationException exception) {

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
