package au.id.raboczi.cornerstone.user_service;

/*-
 * #%L
 * Cornerstone :: User service API
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

import javax.security.auth.login.LoginException;
import org.osgi.service.useradmin.User;

/**
 * Authentication service.
 */
public interface UserService {

    /**
     * @param username  user name
     * @param password  password
     * @return the {@link User} with the given <i>username</i> and <i>password</i>
     * @throws LoginException if there is no such {@link User}
     */
    User authenticate(String username, String password) throws LoginException;
}
