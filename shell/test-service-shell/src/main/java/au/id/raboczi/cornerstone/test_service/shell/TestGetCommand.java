package au.id.raboczi.cornerstone.test_service.shell;

/*-
 * #%L
 * Cornerstone :: Test service shell commands
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

import au.id.raboczi.cornerstone.CallerNotAuthorizedException;
import au.id.raboczi.cornerstone.Callers;
import au.id.raboczi.cornerstone.test_service.TestService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

/**
 * Invoke {@list TestService#getValue} from the Karaf shell.
 */
@Service
@Command(scope       = "test",
         name        = "get",
         description = "Get the test value")
public class TestGetCommand implements Action {

    /** Test service. */
    @Reference
    @SuppressWarnings("nullness")
    private TestService testService;

    /** {@inheritDoc} */
    @Override
    public Object execute() throws Exception {
        try {
            return testService.getValue(Callers.newCaller("dummy", "viewer"));

        } catch (CallerNotAuthorizedException e) {
            return "Permission denied";
        }
    }
}
