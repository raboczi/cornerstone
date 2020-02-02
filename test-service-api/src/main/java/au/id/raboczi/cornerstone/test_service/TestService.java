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

public interface TestService {

    /** OSGi EventAdmin event topic. */
    String EVENT_TOPIC = "au/id/raboczi/cornerstone/test_service/EVENT";

    /** @return the persistent value */
    String getValue();

    /** @param newValue  the new value to persist */
    void setValue(String newValue);
}
