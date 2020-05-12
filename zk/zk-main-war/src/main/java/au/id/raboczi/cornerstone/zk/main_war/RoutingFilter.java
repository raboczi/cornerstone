package au.id.raboczi.cornerstone.zk.main_war;

/*-
 * #%L
 * Cornerstone :: ZK main page (WAR)
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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL routing logic.
 *
 * Intercepts incoming HTTP requests and maps our desired URL namespace to
 * the actual request dispatchers (servlets).  This duplicates the work that
 * <code>web.xml</code> would do, but is able to dynamically register new
 * OSGi services.
 */
public final class RoutingFilter implements Filter {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingFilter.class);

    /** Unused. */
    private @Nullable FilterConfig filterConfig = null;

    @Override
    public void init(final FilterConfig newFilterConfig) throws ServletException {
        this.filterConfig = newFilterConfig;
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    @Override
    @SuppressWarnings("checkstyle:EmptyBlock")
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String servletPath = httpRequest.getServletPath();
        if ("/zkau".equals(servletPath)) {
            // just silently forward to the ZK asynchronous updater
            chain.doFilter(request, response);

        } else if ("/manage-users".equals(servletPath)) {
            // re-route to manageUsers.zul
            LOGGER.info("Main filter, manage users");
            httpRequest.getRequestDispatcher("/au/id/raboczi/cornerstone/useradmin/zk/manageUsers.zul")
                       .forward(request, response);

        } else {
            // log any other request
            LOGGER.info("Main filter, request servlet path {}", httpRequest.getServletPath());
            chain.doFilter(request, response);
        }
    }

    /**
     * Re-route to <code>index.zul</code>.
     */
    private static class Wrapper extends HttpServletRequestWrapper {

        /** @param request  the wrapped request */
        Wrapper(final HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getServletPath() {
            return "/index.zul";
        }
    }
}
