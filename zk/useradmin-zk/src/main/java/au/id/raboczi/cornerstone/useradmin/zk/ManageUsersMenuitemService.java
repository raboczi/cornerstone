package au.id.raboczi.cornerstone.useradmin.zk;

/*-
 * #%L
 * Cornerstone :: User Admin service ZK UI
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
import au.id.raboczi.cornerstone.zk.MenuitemService;
import au.id.raboczi.cornerstone.zk.util.Components;
import java.util.ResourceBundle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;

/**
 * Menu item for user management.
 */
@Component(service = MenuitemService.class, property = {"menubar=main"})
public final class ManageUsersMenuitemService implements MenuitemService {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageUsersMenuitemService.class);

    /** Menu path. */
    private static final String[] PATH = {"file"};

    /** Authorization lookup service. */
    @Reference
    private @Nullable UserAdmin userAdmin;

    @Override
    public String[] getPath() {
        return PATH;
    }

    @Override
    public Menuitem newMenuitem() {
        ClassLoader cl = ManageUsersMenuitemService.class.getClassLoader();
        ResourceBundle labels = ResourceBundle.getBundle("WEB-INF.zk-label", Locales.getCurrent());
        Menuitem menuitem = new Menuitem(labels.getString("manageUsers.menuitem.label"));
        menuitem.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(final Event event) {
                assert cl != null : "@AssumeAssertion(nullness)";
                ((Window) Components.createComponent("au/id/raboczi/cornerstone/useradmin/zk/manageUsers.zul", cl))
                                    .doModal();
            }
        });

        // Initialize the enabled/disabled status
        assert userAdmin != null : "@AssumeAssertion(nullness)";
        boolean notManager = !Users.getCaller(userAdmin).authorization().hasRole("manager");
        menuitem.setDisabled(notManager);

        // Reactively maintain the enabled/disabled status
        Users.observable().subscribe(user -> {
            assert userAdmin != null : "@AssumeAssertion(nullness)";
            LOGGER.info("Useradmin {}", userAdmin);
            LOGGER.info("Caller {}", Users.getCaller(userAdmin));
            assert userAdmin != null : "@AssumeAssertion(nullness)";
            boolean notManager2 = !Users.getCaller(userAdmin).authorization().hasRole("manager");
            menuitem.setDisabled(notManager2);
        });

        return menuitem;
    }
}
