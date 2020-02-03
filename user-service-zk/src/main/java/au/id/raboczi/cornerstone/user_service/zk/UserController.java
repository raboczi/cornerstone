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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

/**
 * Controller for the macro <code>user.zul</code>.
 */
public final class UserController extends SCRSelectorComposer<Button> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /** The model for this controller. */
    @Reference
    private @Nullable UserService userService;

    @Override
    public void doAfterCompose(final Button button) throws Exception {
        super.doAfterCompose(button);
        updateUser();
    }

    /** @param mouseEvent  button click */
    @Listen("onClick = button#button1")
    public void onClickButton(final MouseEvent mouseEvent) {
        LOGGER.info("Click " + mouseEvent);

        @Nullable User user = (User) Sessions.getCurrent().getAttribute(LoginController.USER);
        if (user == null) {
            Window window = createWindow("au/id/raboczi/cornerstone/user_service/zk/login.zul");
            window.doModal();

        } else {
            Sessions.getCurrent().setAttribute(LoginController.USER, null);
            updateUser();
        }
    }

    /**
     * @param path  a ZUL document in the classpath describing a ZK {@link Window}
     * @return a {@link Window} constructed from the ZUL document
     * @throws IllegalArgumentException if <var>path</var> isn't an item in the classpath
     */
    private Window createWindow(final String path) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            assert classLoader != null : "@AssumeAssertion(nullness)";
            InputStream in = classLoader.getResourceAsStream(path);
            if (in == null) {
                throw new IllegalArgumentException(path + " is not in " + classLoader);
            }
            Reader r = new InputStreamReader(in, "UTF-8");
            Window window = (Window) Executions.createComponentsDirectly(r, "zul", null, null);

            return window;

        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid ZUL path: " + path, e);
        }
    }

    private void updateUser() {
        MessageFormat format = new MessageFormat(getLabels().getString("user.logout"), Locales.getCurrent());
        @Nullable User user = (User) Sessions.getCurrent().getAttribute(LoginController.USER);
        @Localized String label = (user == null)
            ? getLabels().getString("user.login")
            : (@Localized String) format.format(new Object[]{user.getName()});
        getSelf().setLabel(label);
    }
}
