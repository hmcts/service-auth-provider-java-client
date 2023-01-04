package uk.gov.hmcts.reform.authorisation.exceptions;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
