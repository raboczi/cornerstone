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

import java.util.ResourceBundle;
import org.checkerframework.checker.i18n.qual.LocalizableKey;
import org.osgi.service.useradmin.Role;
import org.zkoss.util.Locales;

/**
 * Localizing formatter for {@link Role#getType()}.
 */
public abstract class RoleTypeFormatter {

    /**
     * @param type  a {@link Role} type
     * @return localized presentation form of the <var>type</var>
     */
    @SuppressWarnings("i18nformat.key.not.found")
    public static String roleType(final int type) {
        ResourceBundle bundle = ResourceBundle.getBundle("WEB-INF.zk-label", Locales.getCurrent());
        switch (type) {
        case Role.ROLE: return bundle.getString((@LocalizableKey String) "roleType.role");
        case Role.USER: return bundle.getString((@LocalizableKey String) "roleType.user");
        case Role.GROUP: return bundle.getString((@LocalizableKey String) "roleType.group");
        default: return bundle.getString((@LocalizableKey String) "roleType.unknown");
        }
    }
}
