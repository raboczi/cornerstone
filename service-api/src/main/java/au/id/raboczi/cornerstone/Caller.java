package au.id.raboczi.cornerstone;

import org.osgi.service.useradmin.Authorization;

/**
 * All service API methods should have a {@link Caller} parameter and use it to perform authorization.
 */
public interface Caller {

    /** @return authorization context */
    Authorization authorization();
}
