package au.id.raboczi.cornerstone.test_service;

public interface TestService {

    /** @return the persistent value */
    String getValue();

    /** @param newValue  the new value to persist */
    void setValue(final String newValue);
}
