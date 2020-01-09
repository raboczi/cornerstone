package au.id.raboczi.cornerstone.test_service.impl;

import au.id.raboczi.cornerstone.test_service.TestService;
import io.reactivex.rxjava3.core.*;
import static java.util.concurrent.TimeUnit.SECONDS;;
import org.osgi.service.component.annotations.Component;

@Component(service = {TestService.class})
public class TestServiceImpl implements TestService {

    private String value = "Service initial value";
    private Observable<String> observable = Observable.interval(1, SECONDS).map(n -> n.toString());

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Observable<String> getObservable() {
        return observable;
    }

    @Override
    public void setValue(final String newValue) {
        this.value = newValue;
    }
}
