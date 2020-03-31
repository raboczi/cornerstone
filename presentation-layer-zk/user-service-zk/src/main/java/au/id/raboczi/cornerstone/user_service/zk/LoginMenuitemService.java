package au.id.raboczi.cornerstone.user_service.zk;

/*-
 * #%L
 * Cornerstone :: User service ZK UI
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

import au.id.raboczi.cornerstone.zk.MenuitemService;
import org.osgi.service.component.annotations.Component;
import org.zkoss.zul.Menuitem;

/**
 * Menu item for login.
 */
@Component(service = MenuitemService.class,
           property = {"menubar=main"})
public final class LoginMenuitemService implements MenuitemService {

    /** Menu path. */
    private static final String[] PATH = {"file", "session"};

    @Override
    public String[] getPath() {
        return PATH;
    }

    @Override
    public Menuitem newMenuitem() {
        return new LoginMenuitem();
    }
}
