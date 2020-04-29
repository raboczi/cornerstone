package au.id.raboczi.cornerstone.zk.util;

/*-
 * #%L
 * Cornerstone :: ZK utilities
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;

/**
 * {@link Component} factory.
 */
public abstract class Components {

    /**
     * Construct a {@link Component} from a ZUL document in the classpath.
     *
     * This is more type-safe than using {@link Executions} directly.
     *
     * @param path  a ZUL document in the classpath describing a ZK component
     * @param classLoader  which classpath to search
     * @param <T>  the specific type of the described ZK component
     * @return a {@link Component} constructed from the ZUL document
     * @throws IllegalArgumentException if <var>path</var> isn't an item in the classpath
     * @throws ClassCastException if the ZUL doesn't describe the expected type T
     */
    public static <T extends Component> T createComponent(final String path, final ClassLoader classLoader) {
        try {
            InputStream in = classLoader.getResourceAsStream(path);
            if (in == null) {
                throw new IllegalArgumentException(path + " is not in " + classLoader);
            }
            Reader r = new InputStreamReader(in, "UTF-8");
            Component component = Executions.createComponentsDirectly(r, "zul", null, null);

            return (T) component;

        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid ZUL path: " + path, e);
        }
    }
}
