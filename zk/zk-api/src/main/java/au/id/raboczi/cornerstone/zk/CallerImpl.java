package au.id.raboczi.cornerstone.zk;

/*-
 * #%L
 * Cornerstone :: ZK API
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
import org.osgi.service.useradmin.Authorization;

/**
 * A concrete {@link Caller}.
 */
class CallerImpl implements Caller {

    /** Security permissions. */
    private final Authorization authorization;

    /** @param newAuthorization  currently the only content */
    CallerImpl(final Authorization newAuthorization) {
        this.authorization = newAuthorization;
    }


    // Implementation of Caller

    @Override
    public Authorization authorization() {
        return authorization;
    }
}
