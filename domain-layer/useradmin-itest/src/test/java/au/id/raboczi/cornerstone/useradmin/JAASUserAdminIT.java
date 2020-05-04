package au.id.raboczi.cornerstone.useradmin;

/*-
 * #%L
 * Cornerstone :: JAAS user admin service integration tests
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

import java.util.stream.Stream;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.itests.KarafTestSupport;
import org.checkerframework.checker.nullness.qual.Nullable;
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
import org.osgi.service.useradmin.UserAdmin;

/**
 * Integration test for {@link JAASUserAdmin}.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JAASUserAdminIT extends KarafTestSupport {

    /** Additional configuration.  Propagates the <code>project.version</code> system property. */
    @Configuration
    public Option[] config() {
        String projectVersion = System.getProperty("project.version");
        if (projectVersion == null) {
            throw new Error("project.version system property must be set in the pom.xml maven-failsafe-plugin entry");
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
        executeCommand("feature:repo-add mvn:au.id.raboczi.cornerstone/useradmin/" + System.getProperty("project.version") + "/xml/features");
        installAndAssertFeature("useradmin");
        assertServiceAvailable(UserAdmin.class);
    }

    /** Authenticate a valid username and password. */
    @Test
    public void testAuthenticate_valid() throws Exception {
        UserAdmin userAdmin = getOsgiService(UserAdmin.class);
        @Nullable User user = userAdmin.getUser("username", "karaf");
        assert user != null: "@AssumeAssertion(nullness)";
        assert user.hasCredential("password", "karaf");
    }

    /** Authenticate a nonexistent username. */
    @Test
    public void testAuthenticate_badUser() throws Exception {
        UserAdmin userAdmin = getOsgiService(UserAdmin.class);
        assert null == userAdmin.getUser("username", "nonexistent");
    }

    /** Authenticate an existing user with the wrong password. */
    @Test
    public void testAuthenticate_badPassword() throws Exception {
        UserAdmin userAdmin = getOsgiService(UserAdmin.class);
        @Nullable User user = userAdmin.getUser("username", "karaf");
        assert user != null: "@AssumeAssertion(nullness)";
        assert !user.hasCredential("password", "wrong");
    }
}
