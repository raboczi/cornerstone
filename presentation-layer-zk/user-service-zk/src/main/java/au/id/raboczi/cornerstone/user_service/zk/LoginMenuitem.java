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

import au.id.raboczi.cornerstone.zk.Users;
import au.id.raboczi.cornerstone.zk.util.Components;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.User;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;

/**
 * Menu item for login.
 */
public class LoginMenuitem extends Menuitem {

    public LoginMenuitem() {
        super("Login");
    }

    /** @param event  clicked on this menu item */
    @Listen("onClick")
    public void onClick(final MouseEvent event) {
        @Nullable User user = (User) Sessions.getCurrent().getAttribute(Users.USER);
        if (user == null) {
            ClassLoader classLoader = LoginMenuitem.class.getClassLoader();
            assert classLoader != null : "@AssumeAssertion(nullness)";
            Window window =
                Components.createComponent("au/id/raboczi/cornerstone/user_service/zk/login.zul", classLoader);
            window.doModal();

        } else {
            Sessions.getCurrent().removeAttribute(Users.USER);
        }
    }
}
