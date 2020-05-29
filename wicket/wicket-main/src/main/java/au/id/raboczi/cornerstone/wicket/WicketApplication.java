package au.id.raboczi.cornerstone.wicket;

/*-
 * #%L
 * Cornerstone :: Wicket main page
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

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationListener;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 *
 * @see au.id.raboczi.cornerstone.wicket.Start#main(String[])
 */
public class WicketApplication extends WebApplication {

    /** PID of the configuration for this bundle. */
    private static final String CM_PID = "au.id.raboczi.cornerstone.wicket";

    /** Whether the application is in deployment or development mode. */
    private RuntimeConfigurationType configurationType = RuntimeConfigurationType.DEVELOPMENT;

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        // Access OSGi configuration service to initialize configurationType
        BundleContext bundleContext = (BundleContext) getServletContext().getAttribute("osgi-bundlecontext");
        ServiceReference<ConfigurationAdmin> reference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        updateConfiguration(bundleContext.getService(reference));
        bundleContext.ungetService(reference);

        // Register a handler to reload this application if its configuration changes
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("dummy", "value");
        bundleContext.registerService(ConfigurationListener.class, configurationEvent -> {
            if (configurationEvent.getPid().equals(CM_PID)) {
                updateConfiguration(bundleContext.getService(configurationEvent.getReference()));
            }
        }, properties);
    }

    /**
     * Update {@link #configurationType} based on the configuration {@link CM_PID}.
     *
     * @param configurationAdmin  the CM service providing the configuration
     */
    private void updateConfiguration(final ConfigurationAdmin configurationAdmin) {
        try {
            Configuration configuration = configurationAdmin.getConfiguration(CM_PID);
            String s = (String) configuration.getProperties().get("wicket.configuration");
            if (s != null) {
                getServletContext().log("wicket.configuration changing from " + configurationType + " to " + s);
                configurationType = RuntimeConfigurationType.valueOf(s);
            }

        } catch (IOException | RuntimeException e) {
            getServletContext().log("Failed to update wicket.configuration from " + configurationType, e);
        }
    }

    /**
     * {@inheritDoc}.
     *
     * This implementation is controlled by the "wicket.configuration" property of the
     * OSGi Configuration Admin service with PID "au.id.raboczi.cornerstone.wicket".
     * Valid values are the same as {@link RuntimeConfigurationType#valueOf} accepts.
     */
    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return configurationType;
    }
}
