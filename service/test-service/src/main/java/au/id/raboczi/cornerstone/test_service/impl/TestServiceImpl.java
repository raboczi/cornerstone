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

import au.id.raboczi.cornerstone.test_aspect.Secure;
import au.id.raboczi.cornerstone.test_service.TestService;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Demonstrates event handling.
 */
@Component(service  = {TestService.class},
           property = {"service.exported.interfaces=*"})
public final class TestServiceImpl implements TestService {

    /** A generic property on the service. */
    private String value = "Service initial value";

    /** Notifies changes to {@link #value}. */
    @Reference
    private @Nullable EventAdmin eventAdmin;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    @Secure("test:write")
    public void setValue(final String newValue) {
        this.value = newValue;

        // OSGi EventAdmin notification
        Map<String, Object> properties = new HashMap<>();
        properties.put("value", this.value);
        assert eventAdmin != null : "@AssumeAssertion(nullness)";
        eventAdmin.postEvent(new Event(EVENT_TOPIC, properties));
    }
}
