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

import au.id.raboczi.cornerstone.test_service.TestService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** REST endpoint for the test service. */
@Path("/test")
@Component(service = Endpoint.class, property = { "osgi.jaxrs.resource=true" })
public class Endpoint {

    /** The test service. */
    @Reference
    @SuppressWarnings("nullness")
    private TestService testService;

    /** @return the test value */
    @GET
    @Path("/value")
    @Produces("application/json")
    public String getValue() {
        return testService.getValue();
    }
}
