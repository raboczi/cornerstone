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
import java.io.Serializable;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** REST endpoint for the test service. */
abstract class Callers {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Callers.class);

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

    /**
     * Given an HTTP Authorization header, derive the corresponding {@link Caller}.
     *
     * Only Basic authentication is supported.
     *
     * @param authorization  the value of the HTTP Authorization header; only Basic authentication is supported;
     *     pass <code>null</code> for an anonymous caller
     * @param userAdmin  used to authorize the caller
     * @return caller information, possibly the anonymous caller
     * @throws CallerNotAuthenticatedException if the <var>request</var> included bad credentials
     */
    static Caller callerForAuthorizationHeader(final String authorization, final UserAdmin userAdmin)
        throws CallerNotAuthenticatedException {

        // Check whether the request is attempting to authenticate
        if (authorization == null) {
            // Unauthenticated caller
            LOGGER.info("No HTTP Authorization header present; caller is unauthenticated");
            return new CallerImpl(userAdmin.getAuthorization(null));
        }

        // Validate the presence of HTTP Basic authentication
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new CallerNotAuthenticatedException("Unsupported authorization");
        }

        // Validate the HTTP Basic authentication payload
        String base64 = matcher.group("basicAuthorization");
        String decoded = new String(Base64.getDecoder().decode(base64), ISO_8859_1);
        Matcher matcher2 = BASIC_PAYLOAD_PATTERN.matcher(decoded);
        if (!matcher2.matches()) {
            throw new CallerNotAuthenticatedException("Malformed Basic authorization header");
        }

        // Authenticate via the user admin service
        User user = userAdmin.getUser("username", matcher2.group("name"));
        if (user == null || !user.hasCredential("password", matcher2.group("password"))) {
            throw new CallerNotAuthenticatedException("Credentials rejected");
        }
        LOGGER.info("Authorization header present; caller is authenticated");

        return new CallerImpl(userAdmin.getAuthorization(user));
    }

    /**
     * The authenticated caller.
     */
    private static class CallerImpl implements Caller, Serializable {

        /** Security permissions. */
        private final Authorization authorization;

        /** @param newAuthorization  currently the only content */
        CallerImpl(final Authorization newAuthorization) {
            this.authorization = newAuthorization;
        }

        /** @return includes authorization */
        @Override
        public String toString() {
            return super.toString() + "(authorization=" + authorization + ")";
        }

        // Implementation of Caller

        @Override
        public Authorization authorization() {
            return authorization;
        }
    }
}
