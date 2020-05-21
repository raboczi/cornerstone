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

/**
 * Additional user administration API.
 */
public interface UserAdminExtension {

    /**
     * @param userName  the name of a user
     * @param roleName  the name of the role to be added to the <var>user</var>
     */
    void addRole(String userName, String roleName);

    /**
     * @param userName  the name of a user
     * @param roleName  the name of the role to be removed from the <var>user</var>
     */
    void deleteRole(String userName, String roleName);
}
