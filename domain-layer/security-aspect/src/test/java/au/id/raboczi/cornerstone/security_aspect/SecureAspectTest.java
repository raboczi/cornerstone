package au.id.raboczi.cornerstone.security_aspect;

/*-
 * #%L
 * Cornerstone :: Security aspect
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
import au.id.raboczi.cornerstone.impl.CallerImpl;
import org.junit.Test;

/**
 * Test suite for the {@link Secure} annotation.
 */
public class SecureAspectTest {

    /** Test the happy path. */
    @Test
    public void test_happy() throws Exception {
        testMethod(new CallerImpl("user", "requiredRole"));
    }

    /** Test the unhappy path. */
    @Test(expected = CallerNotAuthorizedException.class)
    public void test_unhappy() throws Exception {
        testMethod(new CallerImpl("user"));
    }

    @Secure("requiredRole")
    private void testMethod(Caller caller) throws CallerNotAuthorizedException {
        // null implementation
    }
}
