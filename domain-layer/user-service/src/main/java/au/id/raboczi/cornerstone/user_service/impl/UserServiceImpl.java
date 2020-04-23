package au.id.raboczi.cornerstone.user_service.impl;

/*-
 * #%L
 * Cornerstone :: User service
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

import au.id.raboczi.cornerstone.user_service.UserService;
import java.security.Principal;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Authentication service implementation. */
@Component(service          = {UserService.class},
           configurationPid = "au.id.raboczi.cornerstone.user_service")
public final class UserServiceImpl implements UserService {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    /** @see {@link javax.security.auth.login.Configuration} */
    private String loginConfigurationName = "Uninitialized";

    /** Which {@link Principal} class identifies a user? */
    private Class userPrincipalClass = Object.class;

    /** Which {@link Principal} class identifies a group? */
    private Class groupPrincipalClass = Object.class;

    /** Which {@link Principal} class identifies a role? */
    private Class rolePrincipalClass = Object.class;


    /**
     * Configure this instance.
     *
     * @param context  automatically supplied by OSGi runtime
     * @throws ClassNotFoundException if jaas.userPrincipalClass or
     *     jaas.rolePrincipalClass aren't available
     */
    @Activate
    protected void activate(final ComponentContext context) throws ClassNotFoundException {
        LOGGER.info("Activate with context properties " + context.getProperties());

        this.loginConfigurationName =
            (@NonNull String) context.getProperties().get("jaas.loginConfigurationName");

        this.userPrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.userPrincipalClass"));

        this.groupPrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.groupPrincipalClass"));

        this.rolePrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.rolePrincipalClass"));
    }


    // Implementation of UserService

    @SuppressWarnings("checkstyle:TodoComment")
    @Override
    public User authenticate(final String username, final String password) throws LoginException {
        LoginContext loginContext = new LoginContext(
            loginConfigurationName, new CallbackHandler() {

            public void handle(final Callback[] callbacks)
                throws UnsupportedCallbackException {

                for (Callback callback: callbacks) {
                    if (callback instanceof NameCallback) {
                        ((NameCallback) callback).setName(username);

                    } else if (callback instanceof PasswordCallback) {
                        ((PasswordCallback) callback).setPassword(
                            password.toCharArray()
                        );

                    } else {
                        throw new UnsupportedCallbackException(callback,
                            "Unimplemented callback");
                    }
                }
            }
        });

        loginContext.login();
        try {
            for (Principal principal: loginContext.getSubject().getPrincipals()) {
                LOGGER.info("Principal: " + principal + "  name: " + principal.getName() + "  class: "
                    + principal.getClass());
            }

            final String user = loginContext
                .getSubject()
                .getPrincipals()
                .stream()
                .filter(principal -> userPrincipalClass.isAssignableFrom(principal.getClass()))
                .findAny()  // TODO: validate a unique result
                .get()
                .getName();

            final String[] groups = loginContext
                .getSubject()
                .getPrincipals()
                .stream()
                .filter(principal -> groupPrincipalClass.isAssignableFrom(principal.getClass()))
                .map(principal -> principal.getName())
                .toArray(String[]::new);

            final String[] roles = loginContext
                .getSubject()
                .getPrincipals()
                .stream()
                .filter(principal -> rolePrincipalClass.isAssignableFrom(principal.getClass()))
                .map(principal -> principal.getName())
                .toArray(String[]::new);

            return new UserImpl(user, roles);

            // TODO: failure isn't invoked in the case of a
            // LoginException

        } finally {
            loginContext.logout();
        }
    }
}
