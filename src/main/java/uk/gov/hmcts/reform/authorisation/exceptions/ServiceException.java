package uk.gov.hmcts.reform.authorisation.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
