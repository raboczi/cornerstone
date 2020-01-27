package au.id.raboczi.cornerstone.zk.event.impl;

/*-
 * #%L
 * Cornerstone :: ZK support
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.impl.EventQueueProviderImpl;

public final class TestEventQueueProvider extends EventQueueProviderImpl {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestEventQueueProvider.class);

    @Override
    public <T extends Event> EventQueue<T> lookup(final String name, final Session session, final boolean autocreate) {
        LOGGER.debug("Lookup queue named " + name + " in session " + session + " autocreate " + autocreate);
        return new TestEventQueue(super.lookup(name, session, autocreate));
    }

    @Override
    public <T extends Event> EventQueue<T> lookup(final String name, final String scope, final boolean autocreate) {
        LOGGER.debug("Lookup queue named " + name + " in scope " + scope + " autocreate " + autocreate);
        return new TestEventQueue(super.lookup(name, scope, autocreate));
    }

    @Override
    public <T extends Event> EventQueue<T> lookup(final String name, final WebApp webApp, final boolean autocreate) {
        LOGGER.debug("Lookup queue named " + name + " in web app " + webApp + " autocreate " + autocreate);
        return new TestEventQueue(super.lookup(name, webApp, autocreate));
    }

    @Override
    public boolean remove(final String name, final Session session) {
        LOGGER.debug("Remove queue named " + name + " from session " + session);
        return super.remove(name, session);
    }

    @Override
    public boolean remove(final String name, final String scope) {
        LOGGER.debug("Remove queue named " + name + " from scope " + scope);
        return super.remove(name, scope);
    }

    @Override
    public boolean remove(final String name, final WebApp webApp) {
        LOGGER.debug("Remove queue named " + name + " from web app " + webApp);
        return super.remove(name, webApp);
    }

    /**
     * Wrapper for {@link EventQueue}.
     *
     * @param <T>  the type of event
     */
    public final class TestEventQueue<T extends Event> implements EventQueue<T> {

        /** The wrapped instance. */
        private EventQueue<T> wrapped;

        /** @param newWrapped  an instance to wrap */
        public TestEventQueue(final EventQueue<T> newWrapped) {
            this.wrapped = newWrapped;
            LOGGER.debug("Constructed test event queue, wrapping " + wrapped);
        }


        // Implementation of EventQueue

        @Override public void close() {
            LOGGER.debug("Close"); wrapped.close();
        }

        @Override public boolean isClose() {
            return wrapped.isClose();
        }

        @Override public boolean isSubscribed(final EventListener<T> listener) {
            return wrapped.isSubscribed(listener);
        }

        @Override public void publish(final T event) {
            LOGGER.debug("Publish event " + event); wrapped.publish(event);
        }

        @Override public void subscribe(final EventListener<T> listener) {
            LOGGER.debug("Subscribe to " + listener); wrapped.subscribe(listener);
        }

        @Override public void subscribe(final EventListener<T> listener, final boolean async) {
            LOGGER.debug("Subscribe to " + listener); wrapped.subscribe(listener, async);
        }

        @Override public void subscribe(final EventListener<T> listener, final EventListener<T> callback) {
            LOGGER.debug("Subscribe to " + listener); wrapped.subscribe(listener, callback);
        }

        @Override public boolean unsubscribe(final EventListener<T> listener) {
            LOGGER.debug("Unsubscribe from " + listener); return wrapped.unsubscribe(listener);
        }
    }
}
