package au.id.raboczi.cornerstone.condpermadmin_conditions;

/*-
 * #%L
 * Cornerstone :: Conditional permission admin conditions
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

import java.util.Dictionary;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

/**
 * A parody of {@link org.osgi.service.condpermadmin.BundleLocationCondition}.
 */
@SuppressWarnings("i18n")
public final class BundleLocationCondition implements Condition {

    /** Bundle to test. */
    private Bundle bundle2;

    /** Condition to test. */
    private ConditionInfo conditionInfo2;

    /** Location to match. */
    private Pattern requiredLocationPattern;

    /** Whether to log interactions to standard output. */
    private boolean isLogged;

    /** Whether the condition applies on a mismatch rather than a match. */
    private boolean isNegative;

    /**
     * This constructor is required for {@link Condition} implementations.
     *
     * @param bundle  the bundle to test
     * @param conditionInfo  the condition to test
     * @see org.osgi.service.condpermadmin.ConditionInfo
     */
    public BundleLocationCondition(final Bundle bundle, final ConditionInfo conditionInfo) {
        /*
        System.out.println(getClass().getName() + " constructor"
            + ", bundle=" + bundle
            + ", conditionInfo=" + conditionInfo);
        */
        this.bundle2 = bundle;
        this.conditionInfo2 = conditionInfo;
        this.requiredLocationPattern = toPattern(conditionInfo.getArgs()[0]);
        this.isNegative = conditionInfo.getArgs().length > 1 && conditionInfo.getArgs()[1].equals("!");
        this.isLogged = true; //bundle.getSymbolicName().startsWith("au.id.raboczi.cornerstone");
    }

    /**
     * Convert the simple regex language of OSGi filter expressions into a Java regex pattern.
     *
     * @param pattern  an OSGi filter regex
     * @return a Java regex pattern
     */
    static Pattern toPattern(final String pattern) {
        try {
            return Pattern.compile(pattern);

        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * This static method is Apache Felix's preferred entry point.
     *
     * @param bundle  the bundle to test
     * @param conditionInfo  the condition to test
     * @return an instance of this class
     */
    public static Condition getCondition(final Bundle bundle, final ConditionInfo conditionInfo) {
        return new BundleLocationCondition(bundle, conditionInfo);
    }

    // Methods Implementing Condition

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public boolean isPostponed() {
        return false;
    }

    @Override
    public boolean isSatisfied() {
        if (requiredLocationPattern.matcher(bundle2.getLocation()).matches()) {
            if (isLogged) {
                System.out.println("ALLOW " + bundle2.getLocation());
            }
            return true;
        }

        if (isLogged) {
            System.out.println("Deny " + bundle2.getLocation() + " vs " + requiredLocationPattern);
        }
        return false;
    }

    @Override
    public boolean isSatisfied(final Condition[] conditions, final Dictionary<Object, Object> context) {
        System.out.println(getClass().getName() + " is satisfied, conditions=" + conditions + ", context=" + context);
        return true;
    }
}
