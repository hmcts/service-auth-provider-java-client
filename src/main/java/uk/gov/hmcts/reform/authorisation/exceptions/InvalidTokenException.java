package uk.gov.hmcts.reform.authorisation.exceptions;

public class InvalidTokenException extends AbstractAuthorisationException {

    InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
