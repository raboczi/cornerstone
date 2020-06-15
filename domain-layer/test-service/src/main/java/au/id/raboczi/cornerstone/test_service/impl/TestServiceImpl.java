package au.id.raboczi.cornerstone.test_service.impl;

/*-
 * #%L
 * Cornerstone :: Test service
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
import au.id.raboczi.cornerstone.security_aspect.Secure;
import au.id.raboczi.cornerstone.test_service.TestService;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates event handling.
 */
@Component(service  = {TestService.class, UserAdminListener.class},
           property = {"service.exported.interfaces=*"})
public final class TestServiceImpl implements TestService, UserAdminListener {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

    /** A generic property on the service. */
    private String value = "Service initial value";

    /** Notifies changes to {@link #value}. */
    @Reference
    private @Nullable EventAdmin eventAdmin;


    // Implementation of TestService

    @Override
    @Secure("viewer")
    public String getValue(final Caller caller) throws CallerNotAuthorizedException {
        LOGGER.info("Reading value {} for caller {}", value, caller);

        return value;
    }

    @Override
    @Secure("manager")
    public void setValue(final String newValue, final Caller caller) throws CallerNotAuthorizedException {
        LOGGER.info("Writing value from {} to {} for caller {}", value, newValue, caller);

        this.value = newValue;

        // OSGi EventAdmin notification
        Map<String, Object> properties = new HashMap<>();
        properties.put("value", this.value);
        assert eventAdmin != null : "@AssumeAssertion(nullness)";
        eventAdmin.postEvent(new Event(EVENT_TOPIC, properties));
    }

    @Override
    public String getAnotherValue() {
        LOGGER.info("Reading another value {}", value);

        return value;
    }

    @Override
    public void setAnotherValue(final String newValue) {
        LOGGER.info("Writing another value from {} to {} for caller {}", value, newValue);

        this.value = newValue;

        // OSGi EventAdmin notification
        Map<String, Object> properties = new HashMap<>();
        properties.put("value", this.value);
        assert eventAdmin != null : "@AssumeAssertion(nullness)";
        eventAdmin.postEvent(new Event(EVENT_TOPIC, properties));
    }


    // Implementation of UserAdminListener

    @Override
    public void roleChanged(final UserAdminEvent event) {
        LOGGER.info("Role changed: {}", event);
    }
}
