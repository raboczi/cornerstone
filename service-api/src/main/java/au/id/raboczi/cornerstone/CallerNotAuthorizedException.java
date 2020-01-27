package au.id.raboczi.cornerstone;

/**
 * Thrown to indicate a method call has failed because of an unauthorized {@link Caller}.
 *
 * This enables the invoking code to offer to authenticate the user and retry the method call.
 */
public class CallerNotAuthorizedException extends Exception {

    // no extra functionality
}
