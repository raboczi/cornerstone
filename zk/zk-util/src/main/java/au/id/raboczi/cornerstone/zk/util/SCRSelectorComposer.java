package au.id.raboczi.cornerstone.zk.util;

/*-
 * #%L
 * Cornerstone :: ZK support
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;

/**
 * ZUL component controller with added OSGi support.
 *
 * Like its {@link SelectorComposer} superclass, this supports the injection
 * of ZUL components defined in a view document using annotations from the
 * {@link org.zkoss.zk.ui.select.annotation} package.
 *
 * Additionally, it allows the injection of OSGi services using annotations
 * parodying the {@link org.osgi.service.component.annotations} package.
 *
 * Finally, a <code>labels</code> bean property exposes the <code>zk-label</code>
 * {@link ResourceBundle}.
 *
 * @param <T>  the type of component this controller can <code>apply</code> to
 * @see Reference
 */
public class SCRSelectorComposer<T extends Component> extends SelectorComposer<T> {

    /** Logger.  Named after the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SCRSelectorComposer.class);

    /**
     * This method supports the convention that localized property bundles are
     * found under <code>WEB-INF</code> of each bundle classpath, named with the
     * prefix <code>zk-label</code>.
     *
     * @return localized text catalogue
     */
    public ResourceBundle getLabels() {
        ClassLoader loader = getClass().getClassLoader();
        assert loader != null : "@AssumeAssertion(nullness)";
        ResourceBundle bundle = ResourceBundle.getBundle("WEB-INF.zk-label", Locales.getCurrent(), loader);
        assert bundle != null : "@AssumeAssertion(nullness)";

        return bundle;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation performs dependency injection for fields and methods annotated with {@link Reference}.
     */
    @Override
    public void doAfterCompose(final T comp) throws Exception {
        super.doAfterCompose(comp);

        for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {

            for (Field field: c.getDeclaredFields()) {
                for (Annotation annotation: field.getAnnotations()) {
                    if (Reference.class.equals(annotation.annotationType())) {
                        @Nullable Object object;
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            ParameterizedType p = (ParameterizedType) field.getGenericType();
                            object = findServiceCollection((Class) p.getActualTypeArguments()[0]);
                        } else {
                            object = findService(field.getType());
                        }

                        field.setAccessible(true);
                        field.set(this, object);
                        field.setAccessible(false);
                    }
                }
            }

            for (Method method: c.getDeclaredMethods()) {
                for (Annotation annotation: method.getAnnotations()) {
                    if (Reference.class.equals(annotation.annotationType())) {
                        if (method.getParameterCount() != 1) {
                            throw new Exception("@Reference annotation cannot be applied to "
                                + method.toGenericString()
                                + " because that method does not have exactly one parameter");
                        }

                        @Nullable Object object = findService(method.getParameterTypes()[0]);
                        if (object == null) {
                            throw new Error("Null @Reference unimplemented for " +  method.toGenericString());  // TODO:
                        }

                        method.setAccessible(true);
                        method.invoke(this, object);
                        method.setAccessible(false);
                    }
                }
            }
        }
    }

    /**
     * Construct a {@link Component} from a ZUL document in the bundle's classpath.
     *
     * @param path  a ZUL document in the classpath describing a ZK component
     * @param <T>  the specific type of the described ZK component
     * @return a {@link Component} constructed from the ZUL document
     * @throws IllegalArgumentException if <var>path</var> isn't an item in the classpath
     * @throws ClassCastException if the ZUL doesn't describe the expected type T
     */
    protected <T extends Component> T createComponent(final String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        assert classLoader != null : "@AssumeAssertion(nullness)";

        return Components.createComponent(path, classLoader);
    }

    /** @return the bundle context of the servlet */
    protected BundleContext getBundleContext() {
        BundleContext bundleContext = (BundleContext) getSelf()
            .getDesktop()
            .getWebApp()
            .getServletContext()
            .getAttribute("osgi-bundlecontext");

        return bundleContext;
    }

    /**
     * @param <E>  the type of a desired OSGi service
     * @param clazz  the type of a desired OSGi service
     * @return the unique service of the specified <var>clazz</var>, or <code>null</code> if it doesn't exist
     */
    protected <E> @Nullable E findService(final Class<E> clazz) {
        BundleContext bundleContext = getBundleContext();
        ServiceReference<E> serviceReference = bundleContext.getServiceReference(clazz);
        return (serviceReference == null) ? null : bundleContext.getService(serviceReference);
    }

    /**
     * @param <E>  the type of a desired OSGi service
     * @param clazz  the type of a desired OSGi service
     * @return the collection of services of the specified <var>clazz</var>
     */
    @SuppressWarnings("nullness")
    protected <E> Set<E> findServiceCollection(final Class<E> clazz) {
        LOGGER.info("Find service collection of " + clazz);
        BundleContext bundleContext = getBundleContext();
        try {
            return bundleContext.getServiceReferences(clazz, null)
                                .stream()
                                .map(serviceReference -> bundleContext.getService(serviceReference))
                                .collect(Collectors.toSet());

        } catch (InvalidSyntaxException e) {
            throw new Error("This can't happen", e);
        }
    }
}
