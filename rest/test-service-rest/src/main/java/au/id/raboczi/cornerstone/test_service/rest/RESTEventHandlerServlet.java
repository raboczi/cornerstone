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

import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Installs the {@link RESTEventHandler} into Jetty.
 */
@WebServlet(name = "Example WebSocket Servlet", urlPatterns = { "/example-websocket "})
public class RESTEventHandlerServlet extends WebSocketServlet {

    @Override
    public final void configure(final WebSocketServletFactory factory) {
        factory.register(RESTEventHandler.class);
    }
}
