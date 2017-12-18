package uk.gov.hmcts.reform.authorisation.exceptions;

public class JWTDecodingException extends RuntimeException {

    public JWTDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
