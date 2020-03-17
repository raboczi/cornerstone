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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Authentication service implementation. */
@Component(service  = {UserService.class},
           property = {"service.exported.interfaces=*"})
public final class UserServiceImpl implements UserService {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    /** @see {@link javax.security.auth.login.Configuration} */
    private String loginConfigurationName = "Uninitialized";

    /** Which {@link Principal} class identifies a user? */
    private Class userPrincipalClass = Object.class;

    /** Which {@link Principal} class identifies a role? */
    private Class rolePrincipalClass = Object.class;


    // Property accessors

    /** @param newLoginConfigurationName  JAAS login configuration name */
    public void setLoginConfigurationName(
        final String newLoginConfigurationName) {
        this.loginConfigurationName = newLoginConfigurationName;
    }

    /**
     * @param newUserPrincipalClass  of the various principals associated
     *     with a subject, which one is used as the user id?
     */
    public void setUserPrincipalClass(final Class newUserPrincipalClass) {
        this.userPrincipalClass = newUserPrincipalClass;
    }

    /**
     * @param newRolePrincipalClass  of the various principals associated
     *     with a subject, which ones are used for roles?
     */
    public void setRolePrincipalClass(final Class newRolePrincipalClass) {
        this.rolePrincipalClass = newRolePrincipalClass;
    }


    // OSGi Config service

    /** Configuration. */
    public @interface Config {
        /**
         * @return which login configuration to use
         * @see javax.security.auth.login.Configuration
         */
        String jaas_loginConfigurationName() default "karaf";

        /** @return which {@link Principal} class identifies a group? */
        String jaas_groupPrincipalClass() default "org.apache.karaf.jaas.boot.principal.GroupPrincipal";

        /** @return which {@link Principal} class identifies a role? */
        String jaas_rolePrincipalClass() default "org.apache.karaf.jaas.boot.principal.RolePrincipal";

        /** @return which {@link Principal} class identifies a user? */
        String jaas_userPrincipalClass() default "org.apache.karaf.jaas.boot.principal.UserPrincipal";
    }

    /** @param config  automatically supplied by OSGi runtime */
    @Activate
    protected void activate(final Config config) {
        LOGGER.info("Activate with config " + config);
        setLoginConfigurationName(config.jaas_loginConfigurationName());
        //setGroupPrincipalClass(config.jaas_groupPrincipalClass
    }


    // Implementation of UserService

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
            for (Principal principal: loginContext.getSubject()
                                                  .getPrincipals()) {
                LOGGER.info("Principal: " + principal
                    + "  name: " + principal.getName()
                    + "  class: " + principal.getClass());
            }

            final String user = loginContext
                .getSubject()
                .getPrincipals()
                .stream()
                .filter(principal -> userPrincipalClass
                    .isAssignableFrom(principal.getClass()))
                .findAny()  // TO DO: validate a unique result
                .get()
                .getName();

            final String[] roles = loginContext
                .getSubject()
                .getPrincipals()
                .stream()
                .filter(principal -> rolePrincipalClass
                    .isAssignableFrom(principal.getClass()))
                .map(principal -> principal.getName())
                .toArray(String[]::new);

            return new UserImpl(user, roles);

            // TO DO: failure isn't invoked in the case of a
            // LoginException

        } finally {
            loginContext.logout();
        }
    }
}
