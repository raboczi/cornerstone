package au.id.raboczi.cornerstone.zk.util;

/*-
 * #%L
 * Cornerstone :: ZK support
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//import org.osgi.service.component.annotations.CollectionType;
//import org.osgi.service.component.annotations.FieldOption;
//import org.osgi.service.component.annotations.ReferenceCardinality;
//import org.osgi.service.component.annotations.ReferencePolicy;
//import org.osgi.service.component.annotations.ReferencePolicyOption;
//import org.osgi.service.component.annotations.ReferenceScope;

/**
 * Identify the annotated member or parameter as a reference of a Service
 * Component.
 * <p>
 * When the annotation is applied to a method, the method is the bind method of
 * the reference.
 * <p>
 * When the annotation is applied to a field, the field will contain the bound
 * service(s) of the reference.
 * <p>
 * This annotation is processed at runtime by @{link SCRSelectorComposer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Reference {

    String name() default "";

    Class<?> service() default Object.class;

    //ReferenceCardinality cardinality() default ReferenceCardinality.MANDATORY;

    //ReferencePolicy policy() default ReferencePolicy.STATIC;

    String target() default "";

    //ReferencePolicyOption policyOption() default ReferencePolicyOption.RELUCTANT;

    //ReferenceScope scope() default ReferenceScope.BUNDLE;

    String bind() default "";

    String updated() default "";

    String unbind() default "";

    String field() default "";

    //FieldOption fieldOption() default FieldOption.REPLACE;

    int parameter() default 0;

    //CollectionType collectionType() default CollectionType.SERVICE;
}
