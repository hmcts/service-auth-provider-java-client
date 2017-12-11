package uk.gov.hmcts.reform.authorisation.exceptions;

import feign.FeignException;

public abstract class AbstractAuthorisationException extends RuntimeException {

    AbstractAuthorisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AbstractAuthorisationException parseFeignException(FeignException exception) {
        boolean isClientError = exception.status() >= 400 && exception.status() <= 499;

        if (isClientError) {
            return new InvalidTokenException(exception.getMessage(), exception);
        } else {
            return new ServiceException(exception.getMessage(), exception);
        }
    }
}
