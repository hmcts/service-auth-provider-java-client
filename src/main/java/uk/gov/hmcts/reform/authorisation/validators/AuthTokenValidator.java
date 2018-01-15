package uk.gov.hmcts.reform.authorisation.validators;

import java.util.List;

public interface AuthTokenValidator {

    /**
     * Validate bearer token.
     * @param token Bearer token
     */
    void validate(String token);

    /**
     * Validate bearer token with optional roles.
     * @param token Bearer token
     * @param roles Roles
     */
    void validate(String token, List<String> roles);

    /**
     * Validate bearer token and return service name from it.
     * @param token Bearer token
     * @return Service name
     */
    String getServiceName(final String token);
}
