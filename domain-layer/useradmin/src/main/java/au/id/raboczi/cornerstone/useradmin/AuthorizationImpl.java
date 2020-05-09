package au.id.raboczi.cornerstone.useradmin;

/*-
 * #%L
 * Cornerstone :: User Admin service
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

import java.io.Serializable;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@inheritDoc}. */
class AuthorizationImpl implements Authorization, Serializable {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationImpl.class);

    /** Name of the user, if any. */
    private final @Nullable String name;

    /** Roles of the user. */
    private final String[] roles;

    /**
     * @param userName  <code>null</code> for the unauthorized user
     * @param userRoles  may be empty, but never <code>null</code>
     */
    AuthorizationImpl(final @Nullable String userName, final String[] userRoles) {
        assert userRoles != null;

        this.name  = userName;
        this.roles = userRoles;
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public boolean hasRole(final String roleName) {
        return Arrays.asList(roles).contains(roleName);
    }

    /**
     * @return {@inheritDoc}  This implementation never returns <code>null</code>, instead
     *     returning an empty array if no roles are present.
     */
    @Override
    public String[] getRoles() {
        return roles;
    }

    /** @return includes name and roles */
    @Override
    public String toString() {
        return super.toString() + "(name=" + name + " roles=" + Arrays.asList(roles) + ")";
    }
}
