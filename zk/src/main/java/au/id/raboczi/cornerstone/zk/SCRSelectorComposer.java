package au.id.raboczi.cornerstone.zk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
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
 * @see Reference
 */
public class SCRSelectorComposer<T extends Component> extends SelectorComposer<T> {

    @Override
    public void doAfterCompose(T comp) throws Exception {
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
                            throw new Exception("@Reference annotation cannot be applied to " + method.toGenericString() + " because that method does not have exactly one parameter");
                        }
                        method.setAccessible(true);
                        method.invoke(this, findService(method.getParameterTypes()[0]));
                        method.setAccessible(false);
                    }
                }
            }
        }
    }

    protected BundleContext getBundleContext() {
        BundleContext bundleContext = (BundleContext) getSelf()
            .getDesktop()
            .getWebApp()
            .getServletContext()
            .getAttribute("osgi-bundlecontext");

        return bundleContext;
    }

    /**
     * @param clazz  the type of a desired OSGi service
     * @return the unique service of the specified <var>clazz</var>, or <code>null</code> if it doesn't exist
     */
    protected <E> E findService(Class<E> clazz) {
        BundleContext bundleContext = getBundleContext();
        ServiceReference<E> serviceReference = bundleContext.getServiceReference(clazz);
        E e = bundleContext.getService(serviceReference);

        return e;
    }
}
