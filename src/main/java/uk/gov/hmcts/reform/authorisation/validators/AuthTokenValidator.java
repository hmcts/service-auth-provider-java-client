package uk.gov.hmcts.reform.authorisation.validators;

import java.util.List;

interface AuthTokenValidator {

    String BEARER = "Bearer ";

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
}
