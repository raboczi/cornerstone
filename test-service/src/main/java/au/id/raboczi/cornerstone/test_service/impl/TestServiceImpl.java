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

@Component(service = {TestService.class})
public class TestServiceImpl implements TestService {

    private String value = "Service initial value";
    private ObservableSource<String> observableValue = Observable.interval(3, SECONDS).map(n -> n.toString());

    @Reference
    private EventAdmin eventAdmin;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ObservableSource<String> getObservableValue(Caller caller) {
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
