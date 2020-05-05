package au.id.raboczi.cornerstone.zk.menuitem.login;

/*-
 * #%L
 * Cornerstone :: ZK menu item :: Login
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
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.User;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;

/**
 * Menu item for login.
 *
 * This item toggles between allowing login and logout.
 * It has a secondary function of displaying the authenticated user's name.
 */
public class LoginMenuitem extends Menuitem {

    /** Class loader. */
    @SuppressWarnings("nullness")
    private static final ClassLoader CLASSLOADER = LoginMenuitem.class.getClassLoader();

    @SuppressWarnings("method.invocation.invalid")  // this.setLabel could theoretically be called within constructor
    public LoginMenuitem() {
        super(findLabel(Users.getUser()));

        Users.observable().subscribe(user -> setLabel(findLabel(user.orElse(null))));
    }

    /** @param event  clicked on this menu item */
    @Listen("onClick")
    public void onClick(final MouseEvent event) {
        if (Users.getUser() == null) {
            Window window = (Window)
                Components.createComponent("au/id/raboczi/cornerstone/zk/menuitem/login/login.zul", CLASSLOADER);
            window.doModal();

        } else {
            Users.setUser(null);
        }
    }

    /**
     * Generate label text for the menu item.
     *
     * @param user  the authenticated {@link User}, or <code>null</code> if unauthenticated
     * @return localized text for the menu item label, e.g. "Login" or "Logout user123"
     */
    private static @Localized String findLabel(final @Nullable User user) {
        ResourceBundle labels = ResourceBundle.getBundle("WEB-INF.zk-label", Locales.getCurrent(), CLASSLOADER);
        if (user == null) {
            return labels.getString("loginMenuitem.login");

        } else {
            MessageFormat format = new MessageFormat(labels.getString("loginMenuitem.logout"), Locales.getCurrent());

            return (@Localized String) format.format(new Object[]{user.getName()});
        }
    }
}
