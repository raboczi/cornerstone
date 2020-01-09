package au.id.raboczi.cornerstone.test_service;

import io.reactivex.rxjava3.core.*;

public interface TestService {

    /** @return the persistent value */
    String getValue();

    /** @return the value stream */
    Observable<String> getObservable();

    /** @param newValue  the new value to persist */
    void setValue(final String newValue);
}
