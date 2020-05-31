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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Prevent a method from being invoked unless its {@link au.id.raboczi.cornerstone.Caller}
 * argument is authorized for a specified {@link org.osgi.service.useradmin.Role}.
 * <p>
 * If the caller lacks the specified role,
 * {@link au.id.raboczi.cornerstone.CallerNotAuthorizedException} is thrown.
 * The method must have one {@link au.id.raboczi.cornerstone.Caller} argument as its final
 * parameter, and declare {@link au.id.raboczi.cornerstone.CallerNotAuthorizedException}
 * in its <code>throws</code> clause.
 * <p>
 * As an example of use, the following method will throw
 * {@link au.id.raboczi.cornerstone.CallerNotAuthorizedException} if <var>caller</var>
 * does not have the role (i.e. {@link org.osgi.service.useradmin.Authorization#hasRole})
 * named <code>modifyFoo</code>:
 *
 * <blockquote>
 * <pre>
 * &#x40;Secure("modifyFoo")
 * void incrementFoo(Caller caller) throws CallerNotAuthorizedException {
 *     // ....
 * }
 * </pre>
 * </blockquote>
 *
 * When running under a security manager, the following permissions are required:
 * <blockquote>
 * <pre>
 * ( java.lang.RuntimePermission "accessDeclaredMembers" )
 * ( java.lang.RuntimePermission "getClassLoader" )
 * ( javax.security.auth.AuthPermission "getSubject" )
 * </pre>
 * </blockquote>
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface Secure {
    String value() default "";
}
