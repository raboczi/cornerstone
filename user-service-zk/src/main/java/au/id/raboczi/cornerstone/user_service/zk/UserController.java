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

import au.id.raboczi.cornerstone.user_service.UserService;
import au.id.raboczi.cornerstone.zk.Reference;
import au.id.raboczi.cornerstone.zk.SCRSelectorComposer;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Button;

/**
 * Controller for the macro <code>user.zul</code>.
 */
public final class UserController extends SCRSelectorComposer<Button> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UserController.class);

    /** @return localized text catalogue */
    public ResourceBundle getLabels() {
        ResourceBundle bundle = ResourceBundle.getBundle("au.id.raboczi.cornerstone.user_service.zk.UserController",
                                                         Locales.getCurrent());
        assert bundle != null : "@AssumeAssertion(nullness)";
        return bundle;
    }

    /** The model for this controller. */
    @Reference
    private @Nullable UserService userService;

    @Override
    public void doAfterCompose(final Button button) throws Exception {
        super.doAfterCompose(button);

         button.setLabel(getLabels().getString("login"));
    }

    /** @param mouseEvent  button click */
    @Listen("onClick = button#button1")
    public void onClickButton(final MouseEvent mouseEvent) {
        LOGGER.info("Click " + mouseEvent);
        MessageFormat format = new MessageFormat(getLabels().getString("logout"), Locales.getCurrent());
        getSelf().setLabel(format.format(new Object[]{"Placeholder"}));
    }
}
