package au.id.raboczi.cornerstone.zk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A parody of {@link org.osgi.service.component.annotations.Reference}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Reference {

    /**
     * The selector string that specifies the Components to wire. If empty, 
     * {@link SelectorComposer} will attempt to wire from fellows by name.
     */
    String value() default "";

    /**
     * If true, the component will be rewired when the composer is deserialized 
     * in cluster environment.
     */
    boolean rewireOnActivate() default false;
}
