package au.id.raboczi.cornerstone.zk;

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
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
 * @param <T>  the type of component this controller can <code>apply</code> to
 * @see Reference
 */
public class SCRSelectorComposer<T extends Component> extends SelectorComposer<T> {

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
                        field.setAccessible(true);
                        field.set(this, findService(field.getType()));
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
                        method.setAccessible(true);
                        method.invoke(this, findService(method.getParameterTypes()[0]));
                        method.setAccessible(false);
                    }
                }
            }
        }
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
    protected <E> E findService(final Class<E> clazz) {
        BundleContext bundleContext = getBundleContext();
        ServiceReference<E> serviceReference = bundleContext.getServiceReference(clazz);
        E e = bundleContext.getService(serviceReference);

        return e;
    }
}
