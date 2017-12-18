package uk.gov.hmcts.reform.authorisation.exceptions;

public class JWTDecodingException extends RuntimeException {

    public JWTDecodingException() {
    }

    public JWTDecodingException(String message) {
        super(message);
    }
}
