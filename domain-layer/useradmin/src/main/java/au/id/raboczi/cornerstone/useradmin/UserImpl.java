package au.id.raboczi.cornerstone.useradmin;

/*-
 * #%L
 * Cornerstone :: User Admin service
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

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JAAS-backed user. */
public final class UserImpl implements Serializable, User {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserImpl.class);

    /** Login configuration.  @see javax.security.auth.login.Configuration */
    private String loginConfigurationName;

    /** User name. */
    private final String name;

    /** {@inheritDoc}. */
    private final Dictionary properties = new Hashtable();

    // Constructor

    /**
     * @param username  user name
     * @param newLoginConfigurationName  as per {@link javax.security.auth.login.Configuration}
     */
    public UserImpl(final String username, final String newLoginConfigurationName) {
        this.loginConfigurationName = newLoginConfigurationName;
        this.name = username;
    }


    // Implementation of the Role super-interface

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getType() {
        return Role.USER;
    }

    @Override
    public Dictionary getProperties() {
        return properties;
    }


    // Implementation of the User interface

    /** @throws Error  always, "Not implemented" */
    @Override
    public Dictionary getCredentials() {
        throw new Error("Not implemented");
    }

    @Override
    public boolean hasCredential(final String key, final Object value) {

        // The only credential that exists is "password"
        if (!"password".equals(key)) {
            return false;
        }

        final char[] password = ((String) value).toCharArray();

        // Create JAAS login context
        @Nullable LoginContext loginContext = null;
        try {
            loginContext = new LoginContext(loginConfigurationName, new CallbackHandler() {
                @Override
                public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
                    for (Callback callback: callbacks) {
                        if (callback instanceof NameCallback) {
                            ((NameCallback) callback).setName(name);

                        } else if (callback instanceof PasswordCallback) {
                            ((PasswordCallback) callback).setPassword(password);

                        } else {
                            throw new UnsupportedCallbackException(callback, "Unimplemented callback");
                        }
                    }
                }
            });
        } catch (LoginException e) {
            throw new RuntimeException("Unable to create JAAS login context", e);
        }

        // Return whether login is successful
        try {
            loginContext.login();

            return true;

        } catch (LoginException e) {
            return false;

        } finally {
            try {
                loginContext.logout();

            } catch (LoginException e) {
                LOGGER.warn("Unable to log out cleanly", e);
            }
        }
    }
}
