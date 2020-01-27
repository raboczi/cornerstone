package au.id.raboczi.cornerstone.test_service.impl;

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.test_service.TestService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import java.util.HashMap;
import java.util.Map;
import static java.util.concurrent.TimeUnit.SECONDS;
import org.osgi.service.component.annotations.Component;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Demonstrates event handling approaches.
 */
@Component(service = {TestService.class})
public final class TestServiceImpl implements TestService {

    /** Timer frequency in seconds. */
    private static final long PERIOD = 3;

    /** A generic property on the service. */
    private String value = "Service initial value";

    /** A counter that increments every {@link #PERIOD} seconds. */
    private ObservableSource<String> observableValue = Observable.interval(PERIOD, SECONDS).map(n -> n.toString());

    /** Notifies changes to {@link #value}. */
    @Reference
    private EventAdmin eventAdmin;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ObservableSource<String> getObservableValue(final Caller caller) {
        return observableValue;
    }

    @Override
    public void setValue(final String newValue) {
        this.value = newValue;

        // OSGi EventAdmin notification
        Map<String, Object> properties = new HashMap<>();
        properties.put("value", this.value);
        eventAdmin.postEvent(new Event(EVENT_TOPIC, properties));
    }
}
