package au.id.raboczi.cornerstone.itest;

/*-
 * #%L
 * Cornerstone :: Integration tests
 * %%
 * Copyright (C) 2019 - 2020 Simon Raboczi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import au.id.raboczi.cornerstone.Caller;
import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import au.id.raboczi.cornerstone.Callers;
import au.id.raboczi.cornerstone.test_service.TestService;
import au.id.raboczi.cornerstone.user_service.UserService;
import java.util.stream.Stream;
import javax.security.auth.login.FailedLoginException;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.itests.KarafTestSupport;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;

/**
 * Integration test.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class IT extends KarafTestSupport {

    /** Additional configuration.  Propagates the <code>project.version</code> system property. */
    @Configuration
    public Option[] config() {
        String projectVersion = System.getProperty("project.version");
        if (projectVersion == null) {
            throw new Error("project.version system property must be set in the pom.xml surefire-maven-plugin entry");
        }

        Option[] options = new Option[]{
            KarafDistributionOption.editConfigurationFilePut("etc/system.properties", "project.version", projectVersion)
        };

        return Stream.of(super.config(), options)
                     .flatMap(Stream::of)
                     .toArray(Option[]::new);
    }

    /** Starts the entire web application inside the test container. */
    @Before
    public void before() throws Exception {
        assertServiceAvailable(FeaturesService.class);
        FeaturesService featuresService = getOsgiService(FeaturesService.class);
        executeCommand("feature:repo-add mvn:au.id.raboczi.cornerstone/zk-main-war/" + System.getProperty("project.version") + "/xml/features");
        installAndAssertFeature("zk-main-war");
    }

    /**
     * Confirm that {@link UserService.authenticate} works on the stock "karaf" user.
     */
    @Test
    public void userService_authenticate() throws Exception {

        // Authenticate against the built-in credentials
        assertServiceAvailable(UserService.class);
        UserService userService = getOsgiService(UserService.class);
        User user = userService.authenticate("karaf", "karaf");
        assertNotNull(user);
    }

    /**
     * Confirm that {@link TestService#getValue} and {@link TestService#setValue} work
     * when the {@link Caller} has the required "viewer" and "manager" roles.
     */
    @Test
    public void testService() throws Exception {

        assertServiceAvailable(TestService.class);
        TestService testService = getOsgiService(TestService.class);

        final Caller CALLER = Callers.newCaller("dummy", "viewer", "manager");
        final String VALUE = "Dummy";

        // Write and read back a test value
        //assertEquals("Service initial value", testService.getValue(CALLER));
        testService.setValue(VALUE, CALLER);
        assertEquals(VALUE, testService.getValue(CALLER));
    }

    /**
     * {@link TestService#getValue} should throw {@CallerNotAuthorizedException} if invoked
     * by a {@link Caller} that lacks the "manager" role.
     */
    @Test
    public void testService_setValue_badCredentials() throws Exception {

        assertServiceAvailable(TestService.class);
        TestService testService = getOsgiService(TestService.class);

        final Caller CALLER = Callers.newCaller("dummy");

        try {
            testService.setValue("Placeholder", CALLER);
            fail("Unauthorized invocation of TestService.setValue should fail.");

        } catch (Exception e) {
            // Expected behavior
        }
    }

    /**
     * {@link TestService#getValue} should throw {@CallerNotAuthorizedException} if invoked
     * by a {@link Caller} that lacks the "viewer" role.
     */
    @Test
    public void testService_getValue_badCredentials() throws Exception {

        assertServiceAvailable(TestService.class);
        TestService testService = getOsgiService(TestService.class);

        final Caller CALLER = Callers.newCaller("dummy");

        try {
            assertEquals("Service initial value", testService.getValue(CALLER));
            fail("Unauthorized invocation of TestService.setValue should fail.");

        } catch (Exception e) {
            // Expected behavior
        }
    }
}
