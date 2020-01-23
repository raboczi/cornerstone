package au.id.raboczi.cornerstone.zk.event.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.impl.EventQueueProviderImpl;

public class TestEventQueueProvider extends EventQueueProviderImpl {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestEventQueueProvider.class);

    public TestEventQueueProvider() {
        LOGGER.debug("Constructed test event queue provider");
    }

    @Override
    public <T extends Event> EventQueue<T> lookup(String name, Session session, boolean autocreate) {
        LOGGER.debug("Lookup queue named " + name + " in session " + session + " autocreate " + autocreate);
        return new TestEventQueue(super.lookup(name, session, autocreate));
    }

    @Override
    public <T extends Event> EventQueue<T> lookup(String name, String scope, boolean autocreate) {
        LOGGER.debug("Lookup queue named " + name + " in scope " + scope + " autocreate " + autocreate);
        return new TestEventQueue(super.lookup(name, scope, autocreate));
    }

    @Override
    public <T extends Event> EventQueue<T> lookup(String name, WebApp webApp, boolean autocreate) {
        LOGGER.debug("Lookup queue named " + name + " in web app " + webApp + " autocreate " + autocreate);
        return new TestEventQueue(super.lookup(name, webApp, autocreate));
    }

    @Override
    public boolean remove(String name, Session session) {
        LOGGER.debug("Remove queue named " + name + " from session " + session);
        return super.remove(name, session);
    }

    @Override
    public boolean remove(String name, String scope) {
        LOGGER.debug("Remove queue named " + name + " from scope " + scope);
        return super.remove(name, scope);
    }

    @Override
    public boolean remove(String name, WebApp webApp) {
        LOGGER.debug("Remove queue named " + name + " from web app " + webApp);
        return super.remove(name, webApp);
    }

    public class TestEventQueue<T extends Event> implements EventQueue<T> {

        private EventQueue<T> wrapped;

        public TestEventQueue(EventQueue<T> wrapped) {
            this.wrapped = wrapped;
            LOGGER.debug("Constructed test event queue, wrapping " + wrapped);
        }


        // Implementation of EventQueue

        public void close() { LOGGER.debug("Close"); wrapped.close(); }
        public boolean isClose() { return wrapped.isClose(); }
        public boolean isSubscribed(EventListener<T> listener) { return wrapped.isSubscribed(listener); }
        public void publish(T event) { LOGGER.debug("Publish event " + event); wrapped.publish(event); }
        public void subscribe(EventListener<T> listener) { LOGGER.debug("Subscribe to " + listener); wrapped.subscribe(listener); }
        public void subscribe(EventListener<T> listener, boolean async) { LOGGER.debug("Subscribe to " + listener); wrapped.subscribe(listener, async); }
        public void subscribe(EventListener<T> listener, EventListener<T> callback) { LOGGER.debug("Subscribe to " + listener); wrapped.subscribe(listener, callback); }
        public boolean unsubscribe(EventListener<T> listener) { LOGGER.debug("Unsubscribe from " + listener); return wrapped.unsubscribe(listener); }
    }
}
