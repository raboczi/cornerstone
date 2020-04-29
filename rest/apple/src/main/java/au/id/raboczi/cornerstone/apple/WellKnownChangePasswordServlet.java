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

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Component;

/**
 * @see <a href="https://wicg.github.io/change-password-url/">A Well-Known URL for Changing Passwords</a>
 */
@Component(service = { Servlet.class },
           property = { "alias=/.well-known/change-password", "servlet-name=well-known-change-password" })
public class WellKnownChangePasswordServlet extends HttpServlet {

    /**
     * Location of the change password page.
     *
     * This is relative to the servlet context.
     */
    private static final String CHANGE_PASSWORD_URL = "dummy.html";

    @Override
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {

        try {
            response.sendRedirect(response.encodeRedirectURL(CHANGE_PASSWORD_URL));

        } catch (IllegalArgumentException e) {
            log("Bad value configured for CHANGE_PASSWORD_URL: " + CHANGE_PASSWORD_URL, e);
            response.sendError(response.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
