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
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Controller for <code>createUser.zul</code>.
 */
public final class CreateUserController extends SCRSelectorComposer<Window> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);

    /** The service to manipulate. */
    @Reference
    @SuppressWarnings("nullness")
    private UserAdmin userAdmin;

    /** Username field. */
    @Wire("#usernameTextbox")
    @SuppressWarnings("initialization.fields.uninitialized")
    private Textbox usernameTextbox;

    /** Password field. */
    @Wire("#passwordTextbox")
    @SuppressWarnings("initialization.fields.uninitialized")
    private Textbox passwordTextbox;

    /** @param event  clicked "Create" button */
    @Listen("onClick = #createButton")
    public void onClickCreateButton(final MouseEvent event) {
        Role newuserRole = userAdmin.createRole(usernameTextbox.getValue(), Role.USER);
        getSelf().detach();
    }

    /** @param event  clicked "Cancel" button */
    @Listen("onClick = #cancelButton")
    public void onClickCancelButton(final MouseEvent event) {
        getSelf().detach();
    }
}
