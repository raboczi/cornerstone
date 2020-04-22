package au.id.raboczi.cornerstone.zk.menubar;

/*-
 * #%L
 * Cornerstone :: Test WAR
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
import au.id.raboczi.cornerstone.zk.util.Reference;
import au.id.raboczi.cornerstone.zk.util.SCRSelectorComposer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menupopup;

/**
 * Populate a menubar from the {@link MenuitemService}s present.
 *
 * The menus are localized from the test-war bundle using keys similar to:
 * <pre>
 * menubar.main.file = File
 * </pre>
 *
 * Submenus work in the same way, just with a longer suffix:
 * <pre>
 * menubar.main.file.settings = Settings
 * </pre>
 */
public final class MenubarController extends SCRSelectorComposer<Menubar> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MenubarController.class);

    /** The menu items. */
    @Reference
    private Set<MenuitemService> services = Collections.emptySet();

    @Override
    public void doFinally() {
        Map<String, Menu> menuMap = new HashMap<>();
        for (MenuitemService service: services) {
            try {
                Component parent = getSelf();
                String path = "menubar.main";
                for (String pathElement: service.getPath()) {
                    path += "." + pathElement;

                    if (menuMap.containsKey(path)) {
                        Menu menu = menuMap.get(path);
                        parent = menu.getMenupopup();

                    } else {
                        Menu menu = new Menu(Labels.getLabel(path, path));
                        menuMap.put(path, menu);
                        parent.appendChild(menu);

                        Menupopup menupopup = new Menupopup();
                        menu.appendChild(menupopup);
                        parent = menupopup;
                    }
                }
                parent.appendChild(service.newMenuitem());

            } catch (Exception e) {
                LOGGER.error("Unable to add item using " + service, e);
            }
        }
    }
}
