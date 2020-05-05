package au.id.raboczi.cornerstone.itest;

/*-
 * #%L
 * Cornerstone :: ZK main page (WAR) integration tests
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

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.itests.KarafTestSupport;
import static org.junit.Assert.assertNotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

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

        // Start up the application
        executeCommand("feature:repo-add mvn:au.id.raboczi.cornerstone/zk-main-war/" + System.getProperty("project.version") + "/xml/features");
        installAndAssertFeature("zk-main-war");

/*
        // Start up the webdriver service
        executeCommand("feature:repo-add mvn:au.id.raboczi.cornerstone/webdriver/" + System.getProperty("project.version") + "/xml/features");
        installAndAssertFeature("webdriver");
        assertServiceAvailable(WebDriver.class);
*/
    }

    /** Shut down the browser. */
    @After
    public void after() throws Exception {
    }

    /** Placeholder test. */
    @Test
    public void test() throws Exception {
/*
        WebDriver driver = getOsgiService(WebDriver.class);
        try {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get("http://localhost:8181/cornerstone/");
            driver.findElement(By.xpath("//span[text()='Login']")).click();
        } finally {
            driver.quit();
        }
*/
    }
}
