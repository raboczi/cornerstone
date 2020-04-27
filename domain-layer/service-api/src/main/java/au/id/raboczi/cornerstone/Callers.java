package au.id.raboczi.cornerstone;

/*-
 * #%L
 * Cornerstone :: Service API
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

import au.id.raboczi.cornerstone.impl.CallerImpl;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * {@link Caller} factory.
 */
public abstract class Callers {

    /**
     * @param userName  name of the user, or <code>null</code> if unauthenticated
     * @param roleNames  optional list of roles
     * @return a {@link Caller} with the given authorizations
     */
    public static Caller newCaller(final @Nullable String userName, final String... roleNames) {
        return new CallerImpl(userName, roleNames);
    }
}
