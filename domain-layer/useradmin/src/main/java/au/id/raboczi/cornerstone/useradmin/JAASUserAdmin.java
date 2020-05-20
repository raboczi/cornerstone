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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.security.auth.login.AppConfigurationEntry;
import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.boot.principal.GroupPrincipal;
import org.apache.karaf.jaas.boot.principal.UserPrincipal;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.jaas.modules.BackingEngineFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** {@inheritDoc}
 *
 * This implementation adapts Apache Karaf's JAAS feature to satisfy the {@link UserAdmin} API.
 */
@Component(service = UserAdmin.class, configurationPid = "au.id.raboczi.cornerstone.useradmin")
public final class JAASUserAdmin implements UserAdmin {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JAASUserAdmin.class);

    /** Provides access to Karaf's inbuilt JAAS security system. */
    @Reference
    @SuppressWarnings("initialization.fields.uninitialized")
    private List<BackingEngineFactory> backingEngineFactories;

    /** Used to implement {@link UserAdminListener}s. */
    @Reference
    @SuppressWarnings("initialization.fields.uninitialized")
    private EventAdmin eventAdmin;

    /** JAAS realms. */
    @Reference
    @SuppressWarnings("initialization.fields.uninitialized")
    private List<JaasRealm> jaasRealms;

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
        this.groupPrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.groupPrincipalClass"));

        this.loginConfigurationName =
            (@NonNull String) context.getProperties().get("jaas.loginConfigurationName");

        this.rolePrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.rolePrincipalClass"));

        this.userPrincipalClass =
            Class.forName((@NonNull String) context.getProperties().get("jaas.userPrincipalClass"));
    }

    /**
     * @param entry
     * @return <code>null</code> if not found
     * @see org.apache.karaf.jaas.command.ManageRealmCommand#execute
     */
    private @Nullable BackingEngine getBackingEngine(final AppConfigurationEntry entry) {
        if (backingEngineFactories != null) {
            for (BackingEngineFactory factory : backingEngineFactories) {
                String loginModuleClass = (String) entry.getOptions().get(ProxyLoginModule.PROPERTY_MODULE);
                if (factory.getModuleClass().equals(loginModuleClass)) {
                    return factory.build(entry.getOptions());
                }
            }
        }
        return null;
    }

    /**
     * @return the login module
     * @throws RuntimeException if the app configuration entry or backing engine aren't configured and available
     * @see org.apache.karaf.jaas.command.ManageRealmCommand#execute
     */
    private BackingEngine getBackingEngine() {

        boolean hidden = false;
        String moduleName = null;
        String realmName = loginConfigurationName;

        JaasRealm realm = null;
        AppConfigurationEntry entry = null;

                List<JaasRealm> realms = jaasRealms; //getRealms(hidden);
                if (realms != null && realms.size() > 0) {
                    for (JaasRealm r : realms) {
                        if (r.getName().equals(realmName)) {
                            realm = r;
                            AppConfigurationEntry[] entries = realm.getEntries();
                            if (entries != null) {
                                for (AppConfigurationEntry e : entries) {
                                    String moduleClass = (String) e.getOptions().get(ProxyLoginModule.PROPERTY_MODULE);
                                    if (moduleName == null) {
                                        if (getBackingEngine(e) != null) {
                                            entry = e;
                                            break;
                                        }
                                    } else {
                                        if (moduleName.equals(e.getLoginModuleName())
                                         || moduleName.equals(moduleClass)) {
                                                if (getBackingEngine(e) != null) {
                                                                                                entry = e;
                                                                                                break;
                                                                                        }
                                        }
                                    }
                                }
                                if (entry != null) {
                                    break;
                                }
                            }
                        }
                    }
                }

        if (entry == null) {
            throw new RuntimeException("No app configuration entry");
        }

        @Nullable BackingEngine backingEngine = getBackingEngine(entry);
        if (backingEngine == null) {
            throw new RuntimeException("Backing engine not found");
        }

        return backingEngine;
    }

    private void notify(final UserAdminEvent userAdminEvent) {

        // Derive event topic
        String topic = "org/osgi/service/useradmin/UserAdmin/";
        switch (userAdminEvent.getType()) {
        case UserAdminEvent.ROLE_CREATED:
            topic += "ROLE_CREATED";
            break;

        case UserAdminEvent.ROLE_CHANGED:
            topic += "ROLE_CHANGED";
            break;

        case UserAdminEvent.ROLE_REMOVED:
            topic += "ROLE_REMOVED";
            break;

        default:
            throw new IllegalArgumentException("User admin event with unsupported type: " + userAdminEvent.getType());
        }

        // Create properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("event", userAdminEvent);
        properties.put("role", userAdminEvent.getRole());
        properties.put("role.name", userAdminEvent.getRole().getName());
        properties.put("role.type", userAdminEvent.getRole().getType());
        properties.put("service", userAdminEvent.getServiceReference());
        properties.put("service.id", 0L);
        properties.put("service.objectClass", new String[] {UserAdmin.class.getName()});
        properties.put("service.pid", "dummy");

        eventAdmin.postEvent(new Event(topic, properties));
    }


    // Methods implementing UserAdmin

    @Override
    public @Nullable Role createRole(final String name, final int type) {

        ServiceReference<UserAdmin> serviceReference = (@NonNull ServiceReference<UserAdmin>) null;

        switch (type) {
        case Role.USER:
            getBackingEngine().addUser(name, "password");
            Role userRole = new RoleImpl(name, Role.USER);
            notify(new UserAdminEvent(serviceReference, UserAdminEvent.ROLE_CREATED, userRole));

            return userRole;

        case Role.GROUP:
            getBackingEngine().createGroup(name);
            Role groupRole = new RoleImpl(name, Role.GROUP);
            notify(new UserAdminEvent(serviceReference, UserAdminEvent.ROLE_CREATED, groupRole));

            return groupRole;

        default:
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    @Override
    @SuppressWarnings({"checkstyle:AvoidNestedBlocks", "argument.type.incompatible"})
    public boolean removeRole(final String name) {

        BackingEngine backingEngine = getBackingEngine();
        ServiceReference<UserAdmin> serviceReference = (@NonNull ServiceReference<UserAdmin>) null;

        // Remove the role if it's a user
        {
            @Nullable UserPrincipal userPrincipal = backingEngine.lookupUser(name);
            if (userPrincipal != null) {
                backingEngine.deleteUser(userPrincipal.getName());
                Role userRole = new RoleImpl(userPrincipal.getName(), Role.USER);
                notify(new UserAdminEvent(serviceReference, UserAdminEvent.ROLE_REMOVED, userRole));

                return true;
            }
        }

        // Remove the role if it's a group
        @Nullable GroupPrincipal groupPrincipal = backingEngine
            .listGroups()
            .keySet()
            .stream()
            .filter(groupPrincipal2 -> groupPrincipal2.getName().equals(name))
            .findAny()
            .orElse(null);
        if (groupPrincipal != null) {
            // Deleting the group from every user causes it to be removed, which is weird since groups are created empty
            for (UserPrincipal userPrincipal : backingEngine.listUsers()) {
                backingEngine.deleteGroup(userPrincipal.getName(), groupPrincipal.getName());
            }

            // Notify listeners
            Role groupRole = new RoleImpl(groupPrincipal.getName(), Role.GROUP);
            notify(new UserAdminEvent(serviceReference, UserAdminEvent.ROLE_REMOVED, groupRole));

            return true;
        }

        return false;
    }

    @Override
    public @Nullable Role getRole(final String name) {

        // Predefined roles
        if (Role.USER_ANYONE.equals(name)) {
            return new RoleImpl(name, Role.ROLE);
        }

        // User roles
        BackingEngine backingEngine = getBackingEngine();
        UserPrincipal userPrincipal = backingEngine.lookupUser(name);
        if (userPrincipal != null) {
            return new RoleImpl(name, Role.USER);
        }

        // Group roles
        if (backingEngine.listGroups()
                         .keySet()
                         .stream()
                         .map(groupPrincipal -> groupPrincipal.getName())
                         .collect(Collectors.toSet())
                         .contains(name)) {

            return new RoleImpl(name, Role.GROUP);
        }

        return null;
    }

    @Override
    public @Nullable Role[] getRoles(final String filter) throws InvalidSyntaxException {

        BackingEngine engine = getBackingEngine();

        ArrayList<Role> roles = new ArrayList<>();
        roles.addAll(engine
            .listUsers()
            .stream()
            .map(userPrincipal -> new RoleImpl(userPrincipal.getName(), Role.USER))
            .collect(Collectors.toList()));
        roles.addAll(engine
            .listGroups()
            .keySet()
            .stream()
            .map(groupPrincipal -> new RoleImpl(groupPrincipal.getName(), Role.GROUP))
            .collect(Collectors.toList()));

        return roles.toArray(new Role[] {});
    }

    /** Wrapper to turn a {@link Principal} into a {@link Role}. */
    private class RoleImpl implements Role, Serializable {

        /** Name.  Same as the {@link Principal}. */
        private final String name;

        /** Dictionary.  Always empty. */
        private final Dictionary properties = new Hashtable();

        /** Type.  Depends on the {@link Principal} and this service's configuration. */
        private final int type;

        /**
         * @param newName  new name
         * @param newType  new type
         */
        RoleImpl(final String newName, final int newType) {
            name = newName;
            type = newType;
        }

        /** @param principal  the an instance to wrap */
        @SuppressWarnings("checkstyle:MagicNumber")
        RoleImpl(final Principal principal) {
            name = principal.getName();

            if (userPrincipalClass.isAssignableFrom(principal.getClass())) {
                type = Role.USER;

            } else if (groupPrincipalClass.isAssignableFrom(principal.getClass())) {
                type = Role.GROUP;

            } else if (rolePrincipalClass.isAssignableFrom(principal.getClass())) {
                type = 3;

            } else {
                type = 4;
            }
        }

        // Methods implementing Role

        @Override public String getName() {
            return name;
        }
        @Override public Dictionary getProperties() {
            return properties;
        }
        @Override public int getType() {
            return type;
        }
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

        BackingEngine engine = getBackingEngine();
        UserPrincipal userPrincipal = engine.lookupUser(value);
        if (userPrincipal == null) {
            return null;
        }

        return new UserImpl(userPrincipal.getName(), loginConfigurationName);
    }

    @Override
    public Authorization getAuthorization(final @Nullable User user) {
        if (user == null) {
            return new AuthorizationImpl(null, new String[] {});

        } else {
            BackingEngine engine = getBackingEngine();
            UserPrincipal userPrincipal = engine.lookupUser(user.getName());
            String[] roles = engine
                .listRoles(userPrincipal)
                .stream()
                .map(rolePrincipal -> rolePrincipal.getName())
                .toArray(String[]::new);

            return new AuthorizationImpl(user.getName(), roles);
        }
    }
}
