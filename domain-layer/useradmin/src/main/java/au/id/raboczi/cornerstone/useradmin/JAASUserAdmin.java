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

import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.service.useradmin.UserAdminListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@inheritDoc}
 *
 * This implementation is backed by Apache Karaf's JAAS feature.
 */
@Component(service          = UserAdmin.class,
           configurationPid = "au.id.raboczi.cornerstone.useradmin")
public final class JAASUserAdmin implements UserAdmin {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JAASUserAdmin.class);

    /** The listeners we need to notify about any role changes. */
    @Reference(fieldOption = FieldOption.UPDATE, policy = ReferencePolicy.DYNAMIC)
    @SuppressWarnings("initialization.fields.uninitialized")
    private Set<UserAdminListener> userAdminListeners;


    // Parameters set by OSGi Configuration service

    /** Which {@link Principal} class identifies a group? */
    private Class groupPrincipalClass = Object.class;

    /** @see {@link javax.security.auth.login.Configuration} */
    private String loginConfigurationName = "Uninitialized";

    /** Which {@link Principal} class identifies a role? */
    private Class rolePrincipalClass = Object.class;

    /** Which {@link Principal} class identifies a user? */
    private Class userPrincipalClass = Object.class;

    /**
     * Configure this instance.
     *
     * @param context  automatically supplied by OSGi runtime
     * @throws ClassNotFoundException if jaas.userPrincipalClass, jaas.groupPrincipalClass, or
     *     jaas.rolePrincipalClass aren't available
     */
    @Activate
    protected void activate(final ComponentContext context) throws ClassNotFoundException {
        LOGGER.info("Activate with context properties " + context.getProperties());

        this.groupPrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.groupPrincipalClass"));

        this.loginConfigurationName =
            (@NonNull String) context.getProperties().get("jaas.loginConfigurationName");

        this.rolePrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.rolePrincipalClass"));

        this.userPrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.userPrincipalClass"));
    }


    // Methods implementing UserAdmin

    @Override
    public @Nullable Role createRole(final String name, final int type) {
        throw new Error("Not implemented");
    }

    @Override
    public boolean removeRole(final String name) {
        throw new Error("Not implemented");
    }

    @Override
    public @Nullable Role getRole(final String name) {
        throw new Error("Not implemented");
    }

    @Override
    public @Nullable Role[] getRoles(final String filter) throws InvalidSyntaxException {
        throw new Error("Not implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @param key  {@inheritDoc}  This implementation only accepts "username" as valid.
     */
    @Override
    public @Nullable User getUser(final String key, final String value) {
        if (!"username".equals(key)) {
            return null;
        }

        return new UserImpl(value, loginConfigurationName, rolePrincipalClass);
    }

    @Override
    public Authorization getAuthorization(final @Nullable User user) {
        return new AuthorizationImpl(user, loginConfigurationName, rolePrincipalClass);
    }
}
