package au.id.raboczi.cornerstone.test_service;

import io.reactivex.rxjava3.core.*;

public interface TestService {

    /** OSGi EventAdmin event topic. */
    static final String EVENT_TOPIC = "au/id/raboczi/cornerstone/test_service/EVENT";

    /** @return the persistent value */
    String getValue();

    /** @return the value stream */
    Observable<String> getObservable();

    /** @param newValue  the new value to persist */
    void setValue(final String newValue);
}
