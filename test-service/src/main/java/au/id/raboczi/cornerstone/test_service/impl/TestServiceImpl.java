package au.id.raboczi.cornerstone.test_service.impl;

import au.id.raboczi.cornerstone.test_service.TestService;
import org.osgi.service.component.annotations.Component;

@Component(service = {TestService.class})
public class TestServiceImpl implements TestService {

    private String value = "Service initial value";

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String newValue) {
        this.value = newValue;
    }
}
