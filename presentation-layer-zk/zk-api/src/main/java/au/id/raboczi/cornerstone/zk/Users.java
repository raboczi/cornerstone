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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.zkoss.zk.ui.Sessions;

/**
 * Convenience method for interacting with the ZK session.
 */
public abstract class Users {

    /** ZK session attribute for the authenticated user. */
    public static final String USER = "user";

    /**
     * @param userAdmin  the roles granted to users
     * @return the caller details for the current ZK session
     */
    public static Caller getCaller(final UserAdmin userAdmin) {
        return new CallerImpl(userAdmin.getAuthorization((@Nullable User) Sessions.getCurrent().getAttribute(USER)));
    }
}
