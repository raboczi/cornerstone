package au.id.raboczi.cornerstone.test_service.zk;

/*-
 * #%L
 * Cornerstone :: Test service ZK UI
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

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import au.id.raboczi.cornerstone.test_service.TestService;
import au.id.raboczi.cornerstone.zk.Users;
import au.id.raboczi.cornerstone.zk.util.Reference;
import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import java.util.concurrent.Callable;
import org.checkerframework.checker.i18n.qual.Localized;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

/**
 * Controller for the macro <code>test.zul</code>.
 */
public final class TestController extends SCRSelectorComposer<Component> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    /** The model for this controller. */
    @Reference
    private @Nullable TestService testService;

    /** Authorization lookup service. */
    @Reference
    private @Nullable UserAdmin userAdmin;

    /** The view for this controller. */
    @Wire("#label1")
    private @Nullable Label label1;

    /** The view for this control. */
    @Wire("#button1")
    private @Nullable Button button1;

    /** The view for that control. */
    @Wire("#button2")
    private @Nullable Button button2;

    @SuppressWarnings({"i18n"})
    private static @Localized String fakeLocalizer(final Callable<String> s) {
        try {
            return s.call();

        } catch (CallerNotAuthorizedException e) {
            return "?";

        } catch (Exception e) {
            return "!";
        }
    }

    private Caller getCaller() {
        assert userAdmin != null : "@AssumeAssertion(nullness)";
        return Users.getCaller(userAdmin);
    }

    @Override
    public void doAfterCompose(final Component comp) throws Exception {
        super.doAfterCompose(comp);

        // Initialize the buttons' enabled/disabled status
        assert userAdmin != null : "@AssumeAssertion(nullness)";
        boolean notManager = !Users.getCaller(userAdmin).authorization().hasRole("manager");
        assert button1 != null : "@AssumeAssertion(nullness)";
        button1.setDisabled(notManager);
        assert button2 != null : "@AssumeAssertion(nullness)";
        button2.setDisabled(notManager);

        // Initialize the label text
        assert label1 != null : "@AssumeAssertion(nullness)";
        label1.setValue(fakeLocalizer(() -> {
            assert testService != null : "@AssumeAssertion(nullness)";
            return testService.getValue(getCaller());
        }));

        // Reactively maintain the buttons' enabled/disabled status
        Users.observable().subscribe(user -> {
            assert userAdmin != null : "@AssumeAssertion(nullness)";
            boolean notManager2 = !Users.getCaller(userAdmin).authorization().hasRole("manager");
            assert button1 != null : "@AssumeAssertion(nullness)";
            button1.setDisabled(notManager2);
            assert button2 != null : "@AssumeAssertion(nullness)";
            button2.setDisabled(notManager2);
        });

        // Reactively maintain the label text
        RxOSGi
            .fromTopic(TestService.EVENT_TOPIC, getBundleContext())
            .map(event -> (String) event.getProperty("value"))
            .subscribe(string -> {
                Desktop desktop = comp.getDesktop();
                try {
                    Executions.activate(desktop);
                    try {
                        assert label1 != null : "@AssumeAssertion(nullness)";
                        label1.setValue(fakeLocalizer(() -> string));

                    } finally {
                        Executions.deactivate(desktop);
                    }
                } catch (DesktopUnavailableException e) {
                    LOGGER.warn("Desktop not longer exists", e);
                }
            });
    }

    /**
     * @param mouseEvent  button click
     * @throws CallerNotAuthorizedException if not authorized to set value
     */
    @Listen("onClick = button#button1; onClick = button#button2")
    public void onClickButton(final MouseEvent mouseEvent) throws CallerNotAuthorizedException {
        assert testService != null : "@AssumeAssertion(nullness)";
        testService.setValue(((Button) mouseEvent.getTarget()).getLabel(), getCaller());
    }
}
