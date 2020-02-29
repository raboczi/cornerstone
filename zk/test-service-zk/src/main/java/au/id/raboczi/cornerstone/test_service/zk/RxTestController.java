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

import au.id.raboczi.cornerstone.test_service.RxTestService;
import au.id.raboczi.cornerstone.zk.Users;
import au.id.raboczi.cornerstone.zk.util.Reference;
import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.observers.DefaultObserver;
import java.util.function.Consumer;
import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

/**
 * Controller for the macro <code>rxtest.zul</code>.
 */
public final class RxTestController extends SCRSelectorComposer<Component>
    implements EventListener<ConsumerEvent<String>> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RxTestController.class);

    /** The name of the ZK queue. */
    private static final String QUEUE = "rxtest-consumer-queue-name";

    /** The model for this controller. */
    @Reference
    private @Nullable RxTestService rxTestService;

    /** The roles granted to users. */
    @Reference
    private @Nullable UserAdmin userAdmin;

    /** The view for this controller. */
    @Wire("#label1")
    private @Nullable Label label1;

    @SuppressWarnings({"i18n"})
    private @Localized String fakeLocalizer(final String s) {
        return s;
    }

    @Override
    public void doAfterCompose(final Component comp) throws Exception {
        super.doAfterCompose(comp);
        assert rxTestService != null : "@AssumeAssertion(nullness)";

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

        assert rxTestService != null : "@AssumeAssertion(nullness)";
        assert userAdmin != null : "@AssumeAssertion(nullness)";
        connect(rxTestService.getObservableValue(Users.getCaller(userAdmin)), eventQueue, s -> {
            assert label1 != null : "@AssumeAssertion(nullness)";
            label1.setValue(fakeLocalizer(s));
        });
    }

    /**
     * Forwards RxJava ObservableSource as ZK events.
     *
     * @param <T>  the type of stream
     * @param observableSource  the source
     * @param eventQueue  the channel
     * @param consumer  the destination
     */
    private <T> void connect(final ObservableSource<T> observableSource,
                             final EventQueue eventQueue,
                             final Consumer<T> consumer) {

        observableSource.subscribe(new DefaultObserver<T>() {
            @Override public void onStart() {
                LOGGER.info("Start!");
            }

            @Override public void onNext(final T t) {
                LOGGER.info("Handle RxJava change: " + t);
                eventQueue.publish(new ConsumerEvent(t, consumer));
            }

            @Override public void onError(final Throwable t) {
                LOGGER.error("Test service exception", t);
            }
            @Override public void onComplete() {
                LOGGER.info("Done!");
            }
        });
    }


    // Implementation of ZK EventListener<ConsumerEvent<String>>

    @Override
    public void onEvent(final ConsumerEvent<String> consumerEvent) throws Exception {
        LOGGER.info("Handle ZK event " + consumerEvent);
        consumerEvent.run();
    }
}
