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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

/** {@inheritDoc}
 *
 * This implementation is backed by Apache Karaf's JAAS feature.
 */
@Component(service  = {UserAdmin.class},
           property = {"service.exported.interfaces=*"})
public final class UserAdminImpl implements UserAdmin {

    @Override
    public @Nullable Role createRole(final String name, final int type) {
        throw new Error("Not implemented");
    }

    @Override
    public boolean removeRole(final String name) {
        throw new Error("Not implemented");
    }

    @Override
    public @Nullable Role getRole(final String name) {
        throw new Error("Not implemented");
    }

    @Override
    public @Nullable Role[] getRoles(final String filter) throws InvalidSyntaxException {
        throw new Error("Not implemented");
    }

    @Override
    public @Nullable User getUser(final String key, final String value) {
        throw new Error("Not implemented");
    }

    @Override
    public Authorization getAuthorization(final @Nullable User user) {
        return new AuthorizationImpl(user);
    }
}
