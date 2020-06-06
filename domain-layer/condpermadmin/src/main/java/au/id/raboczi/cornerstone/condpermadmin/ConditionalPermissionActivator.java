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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Install a policy file into the OSGi conditional permission admin service.
 *
 * The path of the policy file must be present in the system property
 * <code>au.id.raboczi.cornerstone.condpermadmin.policy</code>.
 * The file name is relative to <code>$KARAF_HOME</code> is it is not absolute.
 *
 * The format of policy files is documented in the
 * <a href="https://docs.osgi.org/specification/osgi.core/7.0.0/service.condpermadmin.html#i1716478">OSGi
 * Core R7 §50.2.5</a>.
 *
 * The policy file will be installed when this bundle is activated and uninstalled when it is deactivated.
 */
public final class ConditionalPermissionActivator implements BundleActivator {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalPermissionActivator.class);

    /** System property containing the file path of the policy file. */
    public static final String POLICY_PROPERTY = "au.id.raboczi.cornerstone.condpermadmin.policy";

    // Methods implementing BundleActivator

    /** @throws IOException if <code>$KARAF_HOME/etc/permissions.perms</code> can't be read */
    @Override
    public void start(final BundleContext context) throws IOException {

        // Obtain the policy file
        String policy = System.getProperty(POLICY_PROPERTY);
        if (policy == null) {
            throw new RuntimeException("System property " + POLICY_PROPERTY + " is not set");
        }

        // Compose the list of permissions
        List<String> infos = new LinkedList<>();

        // Override any attempt to lock ConditionalPermissionActivator out
        infos.add(
            "ALLOW {"
            + " [" + BundleLocationCondition.class.getName() + " \"" + context.getBundle().getLocation() + "\"]"
            + " (java.security.AllPermission)"
            + "} \"Can't disable condpermadmin\"");

        // Add each rule in permissions.perms
        try (BufferedReader reader = new BufferedReader(new FileReader(policy))) {
            String info = "";
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                if (!trimmed.startsWith("#") && !trimmed.startsWith("//")) {
                    info += line;
                    if (line.contains("}")) {
                        infos.add(info);
                        info = "";
                    }
                }
                line = reader.readLine();
            }
        }

        // Apply the rules
        assignConditionalPermissions(context, infos);
    }

    @Override
    public void stop(final BundleContext context) {
        assignConditionalPermissions(context, Collections.emptyList());
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
    private void assignConditionalPermissions(final BundleContext context, final List<String> s) {
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
