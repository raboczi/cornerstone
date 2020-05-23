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
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import org.osgi.service.event.EventHandler;
import org.osgi.service.http.HttpService;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forward OSGi events to REST clients.
 */
@Component(service   = EventHandler.class,
           immediate = true,
           name      = "example-websocket",
           property  = {EVENT_TOPIC + "=" + TestService.EVENT_TOPIC})
@SuppressWarnings("all")
@WebSocket
public class RESTEventHandler implements EventHandler {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTEventHandler.class);

    @Override
    public final void handleEvent(final Event event) {
        LOGGER.info("Forward OSGi event to REST clients: " + event);
        sessions.stream().forEach(session -> {
            try {
                switch (event.getTopic()) {
                case TestService.EVENT_TOPIC:
                    session.getRemote().sendString((String) event.getProperty("value"));
                    break;

                default:
                    LOGGER.warn("Unsupported topic: " + event.getTopic());
                    break;
                }
            } catch (IOException e) {
                LOGGER.error("Unable to forward OSGi event to REST client", e);
            }
        });
    }


    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @Reference
    private HttpService httpService;

    @Reference
    private TestService testService;

    private static TestService testService2;

    @Reference
    private UserAdmin userAdmin;

    private static UserAdmin userAdmin2;

    @OnWebSocketConnect
    public void onOpen(final Session session)
        throws CallerNotAuthenticatedException, CallerNotAuthorizedException, IOException {

        LOGGER.info("Open websocket upgrade request {}", session.getUpgradeRequest().getHeaders());
        LOGGER.info("Open websocket user admin {}, userAdmin2 {}", userAdmin, userAdmin2);
        Caller caller =
            Callers.callerForAuthorizationHeader(session.getUpgradeRequest().getHeader("Authorization"), userAdmin2);

        session.setIdleTimeout(-1);
        sessions.add(session);
        LOGGER.info("Open websocket handler {}, test service {}, testService2 {}", this, testService, testService2);
        session.getRemote().sendString(testService2.getValue(caller));
    }

    @OnWebSocketClose
    public void onClose(final Session session, int statusCode, final String reason) {
        sessions.remove(session);
    }

    @OnWebSocketError
    public void onError(final Session session, final Throwable error) {
        LOGGER.error("REST websocket error", error);
    }

    @Activate
    public void activate() throws Exception {
        LOGGER.info("Activate websocket handler {}, test service {}, user admin {}", this, testService, userAdmin);
        testService2 = testService;
        userAdmin2 = userAdmin;
        httpService.registerServlet("/example-websocket", new RESTEventHandlerServlet(), null, null);
    }

    @Deactivate
    public void deactivate() throws Exception {
        testService2 = null;
        userAdmin2 = null;
        httpService.unregister("/example-websocket");
    }
}
