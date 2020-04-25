package au.id.raboczi.cornerstone.test_service.zk;

/*-
 * #%L
 * Cornerstone :: Test service ZK UI
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
import au.id.raboczi.cornerstone.zk.CallerImpl;
import au.id.raboczi.cornerstone.zk.Users;
import au.id.raboczi.cornerstone.zk.util.Reference;
import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.event.Event;
import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import org.osgi.service.event.EventHandler;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

/**
 * Controller for the macro <code>test.zul</code>.
 */
public final class TestController extends SCRSelectorComposer<Component>
    implements EventListener<ConsumerEvent<String>> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    /** The name of the ZK queue. */
    private static final String QUEUE = "test-consumer-queue-name";

    /** The model for this controller. */
    @Reference
    private @Nullable TestService testService;

    /** Authorization lookup service. */
    @Reference
    private @Nullable UserAdmin userAdmin;

    /** The view for this controller. */
    @Wire("#label1")
    private @Nullable Label label1;

    @SuppressWarnings({"i18n"})
    private static @Localized String fakeLocalizer(final Callable<String> s) {
        try {
            return s.call();

        } catch (CallerNotAuthorizedException e) {
            return "?";

        } catch (Exception e) {
            return "!";
        }
    }

    private Caller getCaller() {
        @Nullable User user = (User) Sessions.getCurrent().getAttribute(Users.USER);
        assert userAdmin != null : "@AssumeAssertion(nullness)";

        return new CallerImpl(userAdmin.getAuthorization(user));
    }

    @Override
    public void doAfterCompose(final Component comp) throws Exception {
        super.doAfterCompose(comp);
        assert label1 != null : "@AssumeAssertion(nullness)";

        label1.setValue(fakeLocalizer(() -> {
            assert testService != null : "@AssumeAssertion(nullness)";
            return testService.getValue(getCaller());
        }));

        // ZK events
        EventQueue eventQueue = EventQueues.lookup(QUEUE, comp.getDesktop().getSession(), true);
        LOGGER.info("Do after compose, window " + comp
            + ", desktop " + getSelf().getDesktop()
            + ", session " + getSelf().getDesktop().getSession()
            + ", queue " + eventQueue);
        if (eventQueue == null) {
            throw new Exception("ZK event queue " + QUEUE + " not found and could not be created");
        }
        eventQueue.subscribe(this);

        // OSGi events
        connect(TestService.EVENT_TOPIC, eventQueue, (Consumer<String>) s -> {
            LOGGER.info("Updating label " + label1 + " to " + s);
            assert label1 != null : "@AssumeAssertion(nullness)";
            label1.setValue(fakeLocalizer(() -> {
                assert testService != null : "@AssumeAssertion(nullness)";
                return testService.getValue(getCaller());
            }));
        });
    }

    /**
     * Forwards OSGi EventAdmin traffic as ZK events.
     *
     * @param <T>  the type of stream
     * @param topic  the source, the name of an OSGi EventAdmin queue
     * @param eventQueue  the channel
     * @param consumer  the destination
     */
    private <T> void connect(final String topic, final EventQueue eventQueue, final Consumer<T> consumer) {

        Hashtable ht = new Hashtable();
        ht.put(EVENT_TOPIC, new String[] {topic});

        getBundleContext().registerService(EventHandler.class.getName(), new EventHandler() {
            @Override
            public void handleEvent(final Event event) {
                LOGGER.info("Handle OSGi event: " + event);

                switch (event.getTopic()) {
                case TestService.EVENT_TOPIC:
                    eventQueue.publish(new ConsumerEvent<T>((T) event.getProperty("value"), consumer));
                    break;

                default:
                    LOGGER.warn("Unsupported topic: " + event.getTopic());
                    break;
                }
            }
        }, ht);
    }

    /**
     * @param mouseEvent  button click
     * @throws CallerNotAuthorizedException if not authorized to set value
     */
    @Listen("onClick = button#button1; onClick = button#button2")
    public void onClickButton(final MouseEvent mouseEvent) throws CallerNotAuthorizedException {
        assert testService != null : "@AssumeAssertion(nullness)";
        testService.setValue(((Button) mouseEvent.getTarget()).getLabel(), getCaller());
    }


    // Implementation of ZK EventListener<ConsumerEvent<String>>

    @Override
    public void onEvent(final ConsumerEvent<String> consumerEvent) throws Exception {
        LOGGER.info("Handle ZK event " + consumerEvent);
        consumerEvent.run();
    }
}
