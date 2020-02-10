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

import au.id.raboczi.cornerstone.user_service.UserService;
import java.util.stream.Stream;
import javax.security.auth.login.FailedLoginException;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.itests.KarafTestSupport;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.useradmin.User;

/**
 * Integration test for {@link UserService}.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class UserServiceIT extends KarafTestSupport {

    /** Additional configuration.  Currently empty. */
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
        executeCommand("feature:repo-add mvn:au.id.raboczi.cornerstone/test-war/" + System.getProperty("project.version") + "/xml/features");
        installAndAssertFeature("cornerstone-test-war");
        assertServiceAvailable(UserService.class);
    }

    /** Authenticate a valid username and password. */
    @Test
    public void testAuthenticate_valid() throws Exception {
        UserService userService = getOsgiService(UserService.class);
        User user = userService.authenticate("karaf", "karaf");
        assertNotNull(user);
    }

    /** Authenticate a nonexistent username. */
    @Test(expected = FailedLoginException.class)
    public void testAuthenticate_badUser() throws Exception {
        UserService userService = getOsgiService(UserService.class);
        userService.authenticate("baduser", "badpassword");
    }

    /** Authenticate an existing user with the wrong password. */
    @Test(expected = FailedLoginException.class)
    public void testAuthenticate_badPassword() throws Exception {
        UserService userService = getOsgiService(UserService.class);
        userService.authenticate("karaf", "badpassword");
    }
}
