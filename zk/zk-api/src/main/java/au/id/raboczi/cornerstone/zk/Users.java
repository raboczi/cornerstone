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
 * Convenience method for interacting with the ZK session.
 */
public abstract class Users {

    /** ZK session attribute for the authenticated user. */
    public static final String USER = "user";

    /** @return the caller details for the authenticated user */
    public static Caller getCaller() {
        return new Caller() {
            @Override
            public Authorization authorization() {
                throw new Error("Not implemented");
            }
        };
    }
}
