package au.id.raboczi.cornerstone.test_service.impl;

import au.id.raboczi.cornerstone.test_service.TestService;
import org.osgi.service.component.annotations.Component;

@Component(service = {TestService.class})
public class TestServiceImpl implements TestService {

    public String test(final String s) {
        return s + s;
    }
}
