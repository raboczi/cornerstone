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
import au.id.raboczi.cornerstone.user_service.UserService;
import java.io.Serializable;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.security.auth.login.LoginException;
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
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/** REST endpoint for the test service. */
@Path("/test")
@Component(service = Endpoint.class, property = {"osgi.jaxrs.resource=true"})
public class Endpoint {

    /**
     * Regular expression for HTTP Authorization headers.
     *
     * The named capturing group "basicAuthorization" contains the Base64 payload.
     */
    private static final Pattern AUTHORIZATION_PATTERN =
        Pattern.compile("Basic\\s+(?<basicAuthorization>[\\d\\p{Alpha}+/]+=*)");

    /**
     * Regular expression for decoded HTTP Basic authorization payload.
     *
     * The named capturing groups "name" and "password" contain the payload fields.
     */
    private static final Pattern BASIC_PAYLOAD_PATTERN =
        Pattern.compile("(?<name>[^:]*):(?<password>.*)");

    /** The test service. */
    @Reference
    @SuppressWarnings("nullness")
    private TestService testService;

    /** The user admin service. */
    @Reference
    @SuppressWarnings("nullness")
    private UserAdmin userAdmin;

    /** The user authenication service. */
    @Reference
    @SuppressWarnings("nullness")
    private UserService userService;


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

        try {
            return testService.getValue(caller);

        } catch (CallerNotAuthorizedException e) {
            throw new NotAuthorizedException("Permission denied", "Basic");
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

        try {
            testService.setValue(newValue, caller);

        } catch (CallerNotAuthorizedException e) {
            throw new NotAuthorizedException("Permission denied", "Basic");
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
     * @throws WebApplicationException if the <var>request</var> included bad credentials
     */
    private Caller callerOfRequest(final HttpServletRequest request) {

        // Check whether the request is attempting to authenticate
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            // Unauthenticated caller
            return new CallerImpl(userAdmin.getAuthorization(null));
        }

        // Validate the presence of HTTP Basic authentication
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new NotAuthorizedException("Unsupported authorization", "Basic");
        }

        // Validate the HTTP Basic authentication payload
        String base64 = matcher.group("basicAuthorization");
        String decoded = new String(Base64.getDecoder().decode(base64), ISO_8859_1);
        Matcher matcher2 = BASIC_PAYLOAD_PATTERN.matcher(decoded);
        if (!matcher2.matches()) {
            throw new NotAuthorizedException("Malformed Basic authorization header", "Basic");
        }

        // Authenticate via the user service
        try {
            User user = userService.authenticate(matcher2.group("name"), matcher2.group("password"));

            return new CallerImpl(userAdmin.getAuthorization(user));

        } catch (LoginException e) {
            throw new NotAuthorizedException("Credentials rejected", "Basic");
        }
    }

    /**
     * The REST caller.
     */
    private static class CallerImpl implements Caller, Serializable {

        /** Security permissions. */
        private final Authorization authorization;

        /** @param newAuthorization  currently the only content */
        CallerImpl(final Authorization newAuthorization) {
            this.authorization = newAuthorization;
        }


        // Implementation of Caller

        @Override
        public Authorization authorization() {
            return authorization;
        }
    }
}
