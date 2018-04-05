package uk.gov.hmcts.reform.authorisation.validators;

import feign.FeignException;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

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
            api.authorise(token, roles.toArray(new String[roles.size()]));
        } catch (FeignException exception) {
            throw parseFeignException(exception);
        }
    }

    @Override
    public String getServiceName(final String token) {
        try {
            return api.getServiceName(token);
        } catch (FeignException exception) {
            throw parseFeignException(exception);
        }
    }

    private AbstractLoggingException parseFeignException(FeignException exception) {
        boolean isClientError = exception.status() >= 400 && exception.status() <= 499;

        if (isClientError) {
            return new InvalidTokenException(exception.getMessage(), exception);
        } else {
            return new ServiceException(exception.getMessage(), exception);
        }
    }
}
