package au.id.raboczi.cornerstone.zk.main_war;

/*-
 * #%L
 * Cornerstone :: ZK main page (WAR)
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

import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.theme.Themes;

/**
 * Controller for the macro <code>dmimage.zul</code>.
 *
 * Sets the EL expression <code>${arg.colorscheme}</code> to one of the values "light", "dark", or "reactive"
 * depending on the the color scheme of the current ZK theme.
 * "Reactive" themes use the CSS prefers-color-scheme @media query to switch appearance from the browser side.
 */
public final class DarkModeImageController extends SCRSelectorComposer<Component> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DarkModeImageController.class);

    @Override
    public void doBeforeComposeChildren(final Component comp) throws Exception {
        super.doBeforeComposeChildren(comp);

        String colorScheme;
        switch (Themes.getCurrentTheme()) {
        case "chiaroscuro":
        case "chiaroscuro_c":
            colorScheme = "reactive";
            break;

        case "montana":
        case "montana_c":
            colorScheme = "dark";
            break;

        default:
            colorScheme = "light";
        }

        Map arg = Executions.getCurrent().getArg();
        arg.put("colorscheme", colorScheme);
    }
}
