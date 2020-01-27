package au.id.raboczi.cornerstone.test_service;

import au.id.raboczi.cornerstone.Caller;
import io.reactivex.rxjava3.core.ObservableSource;

public interface TestService {

    /** OSGi EventAdmin event topic. */
    String EVENT_TOPIC = "au/id/raboczi/cornerstone/test_service/EVENT";

    /** @return the persistent value */
    String getValue();

    /**
     * @param caller  all callers are authorized
     * @return the value stream
     */
    ObservableSource<String> getObservableValue(Caller caller);

    /** @param newValue  the new value to persist */
    void setValue(String newValue);
}
