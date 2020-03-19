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

import au.id.raboczi.cornerstone.test_service.TestService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import static org.osgi.service.event.EventConstants.EVENT_TOPIC;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forward OSGi events to REST clients.
 */
@Component(service = EventHandler.class,
           property = {EVENT_TOPIC + "=" + TestService.EVENT_TOPIC})
public class RESTEventHandler implements EventHandler {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTEventHandler.class);

    @Override
    public final void handleEvent(final Event event) {
        LOGGER.info("Forward OSGi event to REST clients: " + event);

        /*
        switch (event.getTopic()) {
        case TestService.EVENT_TOPIC:
            eventQueue.publish(new ConsumerEvent<T>((T) event.getProperty("value"), consumer));
            break;

        default:
            LOGGER.warn("Unsupported topic: " + event.getTopic());
            break;
        }
        */
    }
}
