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
 * When applied to methods in the domain layer, checks that the method's
 * {@link au.id.raboczi.cornerstone.Caller} argument is authorized with the
 * permission in the annotation's argument.
 *
 * If the caller lacks the annotated permission,
 * {@link au.id.raboczi.cornerstone.CallerNotAuthorizedException} is thrown.
 * The method must have one {@link au.id.raboczi.cornerstone.Caller} argument
 * and throw {@link au.id.raboczi.cornerstone.CallerNotAuthorizedException},
 * otherwise a compilation will fail.
 *
 * An example of use:
 *
 * <pre>
 * @Secure("foo:write")
 * void incrementFoo(Caller caller) throws CallerNotAuthorizedException {
 *     // ....
 * }
 * </pre>
 */
@Retention(java.lang.annotation.RetentionPolicy.SOURCE)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface Secure {
    String value() default "";
}
