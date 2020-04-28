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
import au.id.raboczi.cornerstone.Callers;
import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import org.junit.Test;

/**
 * Test suite for the {@link Secure} annotation.
 */
public class SecureAspectTest {

    // Test cases

    /** A secured method can be invoked if the caller has the required role. */
    @Test
    public void test_happy() throws Exception {
        secureMethod(Callers.newCaller("user", "requiredRole"));
        secureMethod2("Dummy", Callers.newCaller("user", "requiredRole"));
    }

    /** A secured method can't be invoked if the caller lacks the required role. */
    @Test(expected = CallerNotAuthorizedException.class)
    public void test_unhappy1() throws Exception {
        secureMethod(Callers.newCaller("user"));
    }

    /** A secured method can't be invoked if the caller lacks the required role. */
    @Test(expected = CallerNotAuthorizedException.class)
    public void test_unhappy2() throws Exception {
        secureMethod2("Dummy", Callers.newCaller("user"));
    }

    /** Expect an error if the method lacks a {@link Caller} parameter. */
    @Test(expected = Error.class)
    public void test_badSignature1() throws Exception {
        secureMethodWithoutCallerParameter();
    }

    /** Expect an error if the method {@link Caller} isn't the last parameter. */
    @Test(expected = Error.class)
    public void test_badSignature2() throws Exception {
        secureMethodWithoutTrailingCallerParameter(Callers.newCaller("user"), "Dummy");
    }


    // Object methods to be tested

    /** A secured method requiring the role named "requiredRole". */
    @Secure("requiredRole")
    private void secureMethod(Caller caller) throws CallerNotAuthorizedException {
        // null implementation
    }

    /** A secured method requiring the role named "requiredRole". */
    @Secure("requiredRole")
    private void secureMethod2(String dummy, Caller caller) throws CallerNotAuthorizedException {
        // null implementation
    }

    /** A secured method without a {@link Caller} parameter. */
    @Secure("requiredRole")
    private void secureMethodWithoutCallerParameter() throws CallerNotAuthorizedException {
        // null implementation
    }

    /** A secured method with a {@link Caller} that isn't the last parameter. */
    @Secure("requiredRole")
    private void secureMethodWithoutTrailingCallerParameter(Caller caller, String dummy)
        throws CallerNotAuthorizedException {

        // null implementation
    }
}
