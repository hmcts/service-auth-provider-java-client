package uk.gov.hmcts.reform.authorisation.validators;

import java.util.List;

interface AuthTokenValidator {

    void validate(String token);

    void validate(String token, List<String> roles);
}
