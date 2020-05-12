package au.id.raboczi.cornerstone.zk.util;

/*-
 * #%L
 * Cornerstone :: ZK utilities
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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zk.ui.util.DesktopInit;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.SessionCleanup;
import org.zkoss.zk.ui.util.SessionInit;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * Logs ZK lifecycle callbacks.
 */
public final class LifecycleLoggingListener implements DesktopCleanup, DesktopInit, ExecutionCleanup, ExecutionInit,
    SessionCleanup, SessionInit, WebAppCleanup, WebAppInit {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleLoggingListener.class);

    @Override
    public void cleanup(final Desktop desktop) {
        LOGGER.info("Desktop clean up {}", desktop);
    }

    @Override
    public void init(final Desktop desktop, final Object request) {
        LOGGER.info("Desktop init {}, request {}", desktop, request);
    }

    @Override
    public void cleanup(final Execution execution, final Execution parent, final List<Throwable> errors) {
        LOGGER.debug("Execution clean up {}, parent {}, errors {}", execution, parent, errors);
    }

    @Override
    public void init(final Execution execution, final Execution parent) {
        LOGGER.debug("Execution init {}, parent {}", execution, parent);
    }

    @Override
    public void cleanup(final Session session) {
        LOGGER.info("Session clean up {}", session);
    }

    @Override
    public void init(final Session session, final Object request) {
        LOGGER.info("Session init {}, request {}", session, request);
    }

    @Override
    public void cleanup(final WebApp webApp) {
        LOGGER.info("Web app clean up {}", webApp);
    }

    @Override
    public void init(final WebApp webApp) {
        LOGGER.info("Web app init {}", webApp);
    }
}
