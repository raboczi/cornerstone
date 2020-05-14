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

import au.id.raboczi.cornerstone.util.RxOSGi;
import au.id.raboczi.cornerstone.zk.util.Reference;
import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import java.util.Optional;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Window;

/**
 * Controller for <code>manageUsers.zul</code>.
 */
public final class ManageUsersController extends SCRSelectorComposer<Window> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageUsersController.class);

    /** The service to manipulate. */
    @Reference
    @SuppressWarnings("nullness")
    private UserAdmin userAdmin;

    /** Listbox to display roles. */
    @Wire("#roleListbox")
    @SuppressWarnings("initialization.fields.uninitialized")
    private Listbox roleListbox;

    @Override
    public void doAfterCompose(final Window window) throws Exception {
        super.doAfterCompose(window);

        window.getDesktop().enableServerPush(true);

        roleListbox.setModel(new ListModelArray(userAdmin.getRoles("")));

        RxOSGi.fromTopic("org/osgi/service/useradmin/UserAdmin/ROLE_CREATED", getBundleContext())
              .subscribe(userAdminEvent -> updateRoleListbox());

        RxOSGi.fromTopic("org/osgi/service/useradmin/UserAdmin/ROLE_REMOVED", getBundleContext())
              .subscribe(userAdminEvent -> updateRoleListbox());
    }

    /**
     * Update {@list #roleListbox}.
     *
     * @throws InterruptedException if the current ZK desktop couldn't be made active
     */
    private void updateRoleListbox() throws InterruptedException {
        Desktop desktop = getSelf().getDesktop();
        Executions.activate(desktop);
        try {
            roleListbox.setModel(new ListModelArray(userAdmin.getRoles("")));

        } catch (InvalidSyntaxException e) {
            throw new Error("Bad hardcoded constant", e);

        } finally {
            Executions.deactivate(desktop);
        }
    }

    /** @param event  clicked "Create user" button */
    @Listen("onClick = #createUserButton")
    public void onClickCreateUserButton(final MouseEvent event) {
        Role newuserRole = userAdmin.createRole("newuser", Role.USER);
    }

    /** @param event  clicked "Create group" button */
    @Listen("onClick = #createGroupButton")
    public void onClickCreateGroupButton(final MouseEvent event) {
        Role newgroupRole = userAdmin.createRole("newgroup", Role.GROUP);
    }

    /** @param event  clicked "Delete" button */
    @Listen("onClick = #deleteButton")
    public void onClickDeleteButton(final MouseEvent event) {
        Listitem selectedItem = roleListbox.getSelectedItem();
        if (selectedItem != null) {
            Role selectedRole = (Role) selectedItem.getValue();
            userAdmin.removeRole(selectedRole.getName());
        }
    }

    /** @param event  clicked "Cancel" button */
    @Listen("onClick = #cancelButton")
    public void onClickCancelButton(final MouseEvent event) {
        getSelf().detach();
    }

    /** @param event  selected role listbox */
    @Listen("onSelect = #roleListbox")
    public void onSelectRoleListbox(final SelectEvent event) {
        Optional<Role> selectedRole = event.getSelectedObjects().stream().findAny();
        if (selectedRole.isPresent()) {
            LOGGER.info("Detail {}", selectedRole.get());
        }
    }
}
