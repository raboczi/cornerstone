package au.id.raboczi.cornerstone.test_service;

/*-
 * #%L
 * Cornerstone :: Test service API
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

public interface TestService {

    /** OSGi EventAdmin event topic. */
    String EVENT_TOPIC = "au/id/raboczi/cornerstone/test_service/EVENT";

    /**
     * @param userName  name of the user
     * @param roleNames  optional list of roles
     * @return a dummy {@link Caller} instance with the specified <var>userName</var>
     *     and <var>roleNames</var>
     */
    Caller getCaller(String userName, String... roleNames);

    /**
     * @param caller  caller context
     * @return the persistent value
     * @throws CallerNotAuthorizedException if <var>caller</var> isn't authorized
     */
    String getValue(Caller caller) throws CallerNotAuthorizedException;

    /**
     * @param newValue  the new value to persist
     * @param caller  caller context
     * @throws CallerNotAuthorizedException if <var>caller</var> isn't authorized
     */
    void setValue(String newValue, Caller caller) throws CallerNotAuthorizedException;
}
