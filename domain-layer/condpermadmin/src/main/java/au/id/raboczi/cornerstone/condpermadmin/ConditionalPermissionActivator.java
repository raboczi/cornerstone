package au.id.raboczi.cornerstone.condpermadmin;

/*-
 * #%L
 * Cornerstone :: Conditional permission admin service
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

import java.util.ConcurrentModificationException;
import java.util.List;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage conditional permissions.
 */
public final class ConditionalPermissionActivator implements BundleActivator {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalPermissionActivator.class);

    // Methods implementing BundleActivator

    @Override
    public void start(final BundleContext context) {
        String[] infos = {
            "ALLOW {"
            + "  [org.osgi.service.condpermadmin.BundleLocationCondition \"" + context.getBundle().getLocation() + "\"]"
            + "  (java.security.AllPermission)"
            + "} \"Can't disable condpermadmin\"",

            "ALLOW {"
            + "  (java.security.AllPermission)"
            + "} \"Not blacklisted\""};
        assignConditionalPermissions(context, infos);
    }

    @Override
    public void stop(final BundleContext context) {
        assignConditionalPermissions(context, new String[] {});
    }

    // Private methods

    /**
     * Replace the current conditional permissions.
     *
     * @param context  this bundle
     * @param s  the permissions to assign
     * @throws ConcurrentModificationException if there was a collision with someone else
     *     modifying the conditional permissions at the same time
     */
    private void assignConditionalPermissions(final BundleContext context, final String[] s) {
        ServiceReference<ConditionalPermissionAdmin> sr = context.getServiceReference(ConditionalPermissionAdmin.class);
        try {
            ConditionalPermissionAdmin cpa = context.getService(sr);
            ConditionalPermissionUpdate u = cpa.newConditionalPermissionUpdate();
            List infos = u.getConditionalPermissionInfos();
            infos.clear();
            for (String string: s) {
                infos.add(cpa.newConditionalPermissionInfo(string));
            }
            if (!u.commit()) {
                throw new ConcurrentModificationException("Permissions changed during update");
            }

        } finally {
            context.ungetService(sr);
        }
    }
}
