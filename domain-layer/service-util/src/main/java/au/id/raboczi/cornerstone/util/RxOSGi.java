package au.id.raboczi.cornerstone.util;

/*-
 * #%L
 * Cornerstone :: Common utilities
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

import io.reactivex.Observable;
import java.util.Hashtable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.event.Event;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import org.osgi.service.event.EventHandler;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integrations between RxJava and OSGi.
 */
public abstract class RxOSGi {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RxOSGi.class);

    /**
     * @param topic  an OSGi event admin topic
     * @param bundleContext  the OSGi bundle context upon which the event handler will be registered
     * @return an RxJava Observable
     */
    public static Observable<Event> fromTopic(final String topic, final BundleContext bundleContext) {
        return Observable.fromPublisher(publisherFromTopic(topic, bundleContext));
    }

    private static Publisher<Event> publisherFromTopic(final String topic, final BundleContext bundleContext) {
        return new Publisher<Event>() {
            @Override
            public void subscribe(final Subscriber<? super Event> subscriber) {
                subscriber.onSubscribe(new Subscription() {

                    private @Nullable ServiceRegistration registration = null;

                    @Override
                    public void cancel() {
                        LOGGER.info("Cancel subscription");
                        if (registration != null) {
                            registration.unregister();
                            registration = null;
                        }
                    }

                    @Override
                    public void request(final long dummy) {
                        LOGGER.info("Request subscription, dummy={}", dummy);

                        Hashtable ht = new Hashtable();
                        ht.put(EVENT_TOPIC, new String[] {topic});

                        registration = bundleContext.registerService(
                            EventHandler.class.getName(),
                            (EventHandler) event -> {
                                try {
                                    subscriber.onNext(event);

                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            },
                            ht);
                    }
                });
            }
        };
    }
}
