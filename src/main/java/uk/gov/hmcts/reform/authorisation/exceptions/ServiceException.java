package uk.gov.hmcts.reform.authorisation.exceptions;

public class ServiceException extends AbstractAuthorisationException {

    ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
