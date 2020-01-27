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

import io.reactivex.rxjava3.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;

/**
 * Custom ZK event corresponding to the testService's observable transitions.
 *
 * @param <T>  type of object to be consumed
 */
public class ConsumerEvent<T> extends Event {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerEvent.class);

    /** An object to be consumed. */
    private T t;

    /** A consumer. */
    private Consumer<T> consumer;

    /**
     * @param newT  an object to be consumed
     * @param newConsumer  a consumer
     */
    public ConsumerEvent(final T newT, final Consumer<T> newConsumer) {
        super("consumer-event-name");
        this.t = newT;
        this.consumer = newConsumer;
    }

    /** Consume the object. */
    void run() {
        try {
            consumer.accept(t);

        } catch (Throwable throwable) {
            LOGGER.error("Failed consumer event", throwable);
        }
    }
}
