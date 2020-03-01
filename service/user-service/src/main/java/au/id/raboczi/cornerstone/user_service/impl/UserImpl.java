package au.id.raboczi.cornerstone.user_service.impl;

/*-
 * #%L
 * Cornerstone :: User service
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
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

/**
 * A serializable user record.
 */
public final class UserImpl implements Serializable, User {

    /** User name. */
    private final String name;

    /** User properties.  The only current property is "roles". */
    private final Hashtable properties = new Hashtable();

    /** User credentials.  Currently always empty. */
    private final Hashtable credentials = new Hashtable();

    /**
     * @param newName  user name
     * @param roles  the names of the roles this user fills
     */
    public UserImpl(final String newName, final String[] roles) {
        this.name = newName;
        this.properties.put("roles", roles);
    }

    // Role

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getType() {
        return Role.USER;
    }

    @Override
    public Dictionary getProperties() {
        return properties;
    }

    // User

    @Override
    public Dictionary getCredentials() {
        return credentials;
    }

    @Override
    public boolean hasCredential(final String key,
                                 final Object value) {
        return value.equals(credentials.get(key));
    }
}
