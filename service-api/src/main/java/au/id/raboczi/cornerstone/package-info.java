/**
 * Common API for services in the business logic layer.
 *
 * Service authorization is performed in the business logic layer.
 * Each service method should include a {@link Caller} parameter and use it to authorize access.
 * If access is denied but might succeed upon reauthentication, this should be indicated by
 * throwing {@link CallerNotAuthorizedException}.
 * This gives the presentation layer the option of offering to authenticate the user and retry.
 */
package au.id.raboczi.cornerstone;
