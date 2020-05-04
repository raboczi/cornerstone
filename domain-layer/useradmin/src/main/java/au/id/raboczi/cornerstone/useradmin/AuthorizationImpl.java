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
import java.util.Arrays;
/*
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
*/
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@inheritDoc}
 *
 * This implementation assumes a @{link User}'s roles are in the user property "roles".
 */
class AuthorizationImpl implements Authorization, Serializable {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationImpl.class);

    /** Name of the user, if any. */
    private final @Nullable String name;

    /** Roles of the user. */
    private /*final*/ String[] roles;

    /**
     * @param user  if <code>null</code>, the unauthorized user
     * @param loginConfigurationName  as per {@link javax.security.auth.login.Configuration}
     * @param rolePrincipalClass  the {@link java.security Principal} class for roles
     */
    AuthorizationImpl(final @Nullable User user, final String loginConfigurationName, final Class rolePrincipalClass) {
        LOGGER.info("user=" + user + " config={} role={}", loginConfigurationName, rolePrincipalClass);

        if (user == null) {
            this.name = null;
            this.roles = new String[] {};

        } else {
            this.name = user.getName();
            this.roles = (String @NonNull []) user.getProperties().get("roles");  // Kludge until the code below works

/*
            // Create JAAS login context
            @Nullable LoginContext loginContext = null;
            try {
                LOGGER.info("Creating login context");
                loginContext = new LoginContext(loginConfigurationName, new CallbackHandler() {
                    @Override
                    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
                        for (Callback callback: callbacks) {
                            if (callback instanceof NameCallback) {
                                ((NameCallback) callback).setName("karaf");

                            } else if (callback instanceof PasswordCallback) {
                                ((PasswordCallback) callback).setPassword("karaf".toCharArray());

                            } else {
                                throw new UnsupportedCallbackException(callback, "Unimplemented callback");
                            }
                        }
                    }
                });
                LOGGER.info("Created login context {}", loginContext);

            } catch (LoginException e) {
                LOGGER.warn("Unable to create login context", e);
                //throw new RuntimeException("Unable to create JAAS login context", e);
            }

            assert loginContext != null : "@AssumeAssertion(nullness)";

            LOGGER.info("Subject " + loginContext.getSubject());

            this.roles = loginContext
                .getSubject()
                .getPrincipals()
                .stream()
                .filter(principal -> rolePrincipalClass.isAssignableFrom(principal.getClass()))
                .map(principal -> principal.getName())
                .toArray(String[]::new);

            LOGGER.info("Functional evaluation complete, roles=" + Arrays.asList(this.roles));
*/
        }
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public boolean hasRole(final String roleName) {
        return Arrays.asList(roles).contains(roleName);
    }

    /* {@inheritDoc}
     *
     * This implementation never returns <code>null</code>, instead
     * returning and empty array if no roles are present.
     */
    @Override
    public String[] getRoles() {
        return roles;
    }

    /** @return includes name and roles */
    @Override
    public String toString() {
        return super.toString() + "(name=" + name + " roles=" + Arrays.asList(roles) + ")";
    }
}
