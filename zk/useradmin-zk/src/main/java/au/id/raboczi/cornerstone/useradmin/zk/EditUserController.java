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

import au.id.raboczi.cornerstone.zk.util.Reference;
import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.event.MouseEvent;
//import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Controller for <code>editUser.zul</code>.
 */
public final class EditUserController extends SCRSelectorComposer<Window> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditUserController.class);

    /** The user to edit. */
    @SuppressWarnings("nullness")
    private Role userRole = (Role) Executions.getCurrent().getArg().get("user");

    /** The service to manipulate. */
    @Reference
    @SuppressWarnings("nullness")
    private UserAdmin userAdmin;

    /** Username field. */
    @Wire("#usernameTextbox")
    @SuppressWarnings("initialization.fields.uninitialized")
    private Textbox usernameTextbox;

    @Override
    public void doAfterCompose(final Window window) throws Exception {
        super.doAfterCompose(window);

        usernameTextbox.setValue(userRole.getName());
        /*
        roleListbox.setModel(new ListModelArray(userAdmin.getRoles("")));

        window.getDesktop().enableServerPush(true);

        RxOSGi.fromTopic("org/osgi/service/useradmin/UserAdmin/ROLE_CREATED", getBundleContext())
              .subscribe(userAdminEvent -> updateRoleListbox(),
                         throwable -> LOGGER.error("Unable to handle role creation", throwable));

        RxOSGi.fromTopic("org/osgi/service/useradmin/UserAdmin/ROLE_REMOVED", getBundleContext())
              .subscribe(userAdminEvent -> updateRoleListbox(),
                         throwable -> LOGGER.error("Unable to handle role removal", throwable));
        */
    }
}
