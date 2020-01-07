package au.id.raboczi.cornerstone.test_war;

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
 * from the {@link org.osgi.service.component.annotations} package.
 */
public class SCRSelectorComposer<T extends Component> extends SelectorComposer<T> {

    protected <E> E findService(Class<E> clazz) {
        BundleContext bundleContext = (BundleContext) getSelf()
            .getDesktop()
            .getWebApp()
            .getServletContext()
            .getAttribute("osgi-bundlecontext");
        ServiceReference<E> serviceReference = bundleContext.getServiceReference(clazz);
        E e = bundleContext.getService(serviceReference);
        return e;
    }
}
