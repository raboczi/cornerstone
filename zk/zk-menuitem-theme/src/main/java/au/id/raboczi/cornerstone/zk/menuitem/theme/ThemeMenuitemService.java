package au.id.raboczi.cornerstone.zk.menuitem.theme;

/*-
 * #%L
 * Cornerstone :: ZK menu item :: Theme
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

import au.id.raboczi.cornerstone.zk.MenuitemService;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.osgi.service.component.annotations.Component;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.theme.Themes;

/**
 * Menu item for selecting the ZK theme.
 */
@Component(service = MenuitemService.class,
           property = {"menubar=main"})
public final class ThemeMenuitemService implements MenuitemService {

    /** Menu path. */
    private static final String[] PATH = {"view"};

    @Override
    public String[] getPath() {
        return PATH;
    }

    @Override
    public Menu newMenuitem() {
        String[] themes = Themes.getThemes();
        Arrays.sort(themes, (a, b) -> Themes.getDisplayName(a).compareTo(Themes.getDisplayName(b)));

        Menupopup menupopup = new Menupopup();
        for (String theme: themes) {
            Menuitem menuitem = new Menuitem(Themes.getDisplayName(theme));
            menuitem.setChecked(theme.equals(Themes.getCurrentTheme()));
            menuitem.setCheckmark(true);
            menuitem.addEventListener("onClick", new EventListener() {
                public void onEvent(final Event event) {
                    Themes.setTheme(Executions.getCurrent(), theme);
                    Executions.sendRedirect("");
                }
            });
            menupopup.appendChild(menuitem);
        }

        ClassLoader classLoader = ThemeMenuitemService.class.getClassLoader();
        assert classLoader != null : "@AssumeAssertion(nullness)";
        ResourceBundle labels = ResourceBundle.getBundle("WEB-INF.zk-label", Locales.getCurrent(), classLoader);
        Menu menu = new Menu(labels.getString("theme.menu.label"));
        menu.appendChild(menupopup);

        return menu;
    }
}
