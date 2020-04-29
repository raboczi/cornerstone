package au.id.raboczi.cornerstone.apple;

/*-
 * #%L
 * Cornerstone :: Apple-proprietary support
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

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.osgi.service.component.annotations.Component;

/**
 * REST endpoint for the Apple app site association service.
 *
 * Note that redirection is forbidden for AASA.
 *
 * @see <a href="https://developer.apple.com/documentation/safariservices/supporting_associated_domains_in_your_app"
 *     >Supporting Associated Domains in Your App</a>
 */
@Path("/.well-known/apple-app-site-association")
@Component(service = AppleAppSiteAssociationEndpoint.class, property = {"osgi.jaxrs.resource=true"})
public class AppleAppSiteAssociationEndpoint {

    /** Site app association object. */
    private final Map<String, Object> aasa = init();

    /** @return a freshly-constructed site app association object */
    private static Map<String, Object> init() {
        String applicationId = "foo";

        Map detail = new HashMap();
        detail.put("appIDs", new String[] {applicationId});

        Map applinks = new HashMap();
        applinks.put("details", new Map[] {detail});

        Map webcredentials = new HashMap();
        webcredentials.put("apps", new String[] {applicationId});

        Map<String, Object> map = new HashMap<>();
        map.put("applinks", applinks);
        map.put("webcredentials", webcredentials);

        return map;
    }


    // REST services

    /**
     * @return the site app association object
     */
    @GET
    @Path("")
    @Produces(APPLICATION_JSON)
    public Map<String, Object> getValue() {
        return aasa;
    }
}
