package au.id.raboczi.cornerstone.test_service;

import io.reactivex.rxjava3.core.ObservableSource;

public interface TestService {

    /** OSGi EventAdmin event topic. */
    static final String EVENT_TOPIC = "au/id/raboczi/cornerstone/test_service/EVENT";

    /** @return the persistent value */
    String getValue();

    /** @return the value stream */
    ObservableSource<String> getObservableValue();

    /** @param newValue  the new value to persist */
    void setValue(final String newValue);
}
