package au.id.raboczi.cornerstone.impl;

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

import au.id.raboczi.cornerstone.Caller;
import java.io.Serializable;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.Authorization;

/**
 * A concrete {@link Caller}.
 *
 * This class has to be {@link Serializable} because that's a requirement to pass it
 * as a parameter to methods over DOSGi, and it's a parameter to every method in the
 * domain layer requiring authorization.
 */
public final class CallerImpl implements Authorization, Caller, Serializable {

    /** User name. */
    private final @Nullable String name;

    /** User roles. */
    private final String[] roles;

    /**
     * @param userName  name of the user, or <code>null</code> if unauthenticated
     * @param roleNames  optional list of roles
     */
    public CallerImpl(final @Nullable String userName, final String... roleNames) {
        this.name = userName;
        this.roles = roleNames;
    }


    // Implementation of Caller

    @Override
    public Authorization authorization() {
        return this;
    }


    // Implementation of Authorization

    @SuppressWarnings("nullness")
    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public boolean hasRole(final String roleName) {
        return Arrays.asList(roles).contains(roleName);
    }

    @Override
    public String[] getRoles() {
        return roles;
    }
}
