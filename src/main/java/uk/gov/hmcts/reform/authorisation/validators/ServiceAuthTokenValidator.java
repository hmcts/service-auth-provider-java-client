package uk.gov.hmcts.reform.authorisation.validators;

import feign.FeignException;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;

import java.util.Collections;
import java.util.List;

public class ServiceAuthTokenValidator implements AuthTokenValidator {

    private final ServiceAuthorisationApi api;

    public ServiceAuthTokenValidator(ServiceAuthorisationApi api) {
        this.api = api;
    }

    @Override
    public void validate(final String token) {
        validate(token, Collections.emptyList());
    }

    @Override
    public void validate(String token, final List<String> roles) {
        try {
            if (!token.startsWith(BEARER)) {
                token = BEARER + token;
            }

            api.authorise(token, roles.toArray(new String[roles.size()]));
        } catch (FeignException exception) {
            boolean isClientError = exception.status() >= 400 && exception.status() <= 499;

            throw isClientError ? new InvalidTokenException(exception.getMessage(), exception) : exception;
        }
    }
}
