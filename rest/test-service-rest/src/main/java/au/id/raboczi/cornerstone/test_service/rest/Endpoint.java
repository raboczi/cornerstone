package au.id.raboczi.cornerstone.test_service.rest;

/*-
 * #%L
 * Cornerstone :: Test service REST UI
 * %%
 * Copyright (C) 2019 - 2020 Simon Raboczi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import au.id.raboczi.cornerstone.test_service.TestService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** REST endpoint for the test service. */
@Path("/test")
@Component(service = Endpoint.class, property = {"osgi.jaxrs.resource=true"})
public class Endpoint {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    /** The test service. */
    @Reference
    @SuppressWarnings("nullness")
    private TestService testService;

    /** The user admin service. */
    @Reference
    @SuppressWarnings("nullness")
    private UserAdmin userAdmin;


    // REST services

    /**
     * @param request  HTTP GET with BASIC authentication
     * @return the test value
     */
    @GET
    @Path("/value")
    @Produces(APPLICATION_JSON)
    public String getValue(final @Context HttpServletRequest request) {
        Caller caller = callerOfRequest(request);
        LOGGER.info("REST get value for caller {}", caller);

        try {
            return testService.getValue(caller);

        } catch (CallerNotAuthorizedException e) {
            throw new NotAuthorizedException("Permission denied", "Basic", e);
        }
    }

    /**
     * @param newValue the new test value
     * @param request  HTTP GET with BASIC authentication
     */
    @POST
    @Path("/value")
    @Consumes(APPLICATION_JSON)
    public void setValue(final String newValue, final @Context HttpServletRequest request) {
        Caller caller = callerOfRequest(request);
        LOGGER.info("REST set value for caller {}", caller);

        try {
            testService.setValue(newValue, caller);

        } catch (CallerNotAuthorizedException e) {
            throw new NotAuthorizedException("Permission denied", "Basic", e);
        }
    }


    // Internal methods

    /**
     * Convert HTTP requests to callers.
     *
     * Because the domain logic layer is stateless, every HTTP request must have an HTTP Basic
     * Authorization header, otherwise it will be treated as the unauthenticated caller.
     *
     * @param request  an arbitrary HTTP request
     * @return caller information, possibly the anonymous caller
     * @throws NotAuthorizedException if the <var>request</var> included bad credentials
     */
    private Caller callerOfRequest(final HttpServletRequest request) {
        try {
            return Callers.callerForAuthorizationHeader(request.getHeader("Authorization"), userAdmin);

        } catch (CallerNotAuthenticatedException e) {
            throw new NotAuthorizedException("Unable to authenticate", "Basic", e);
        }
    }
}
