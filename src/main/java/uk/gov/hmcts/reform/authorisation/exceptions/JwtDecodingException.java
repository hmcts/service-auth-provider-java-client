package uk.gov.hmcts.reform.authorisation.exceptions;

public class JwtDecodingException extends RuntimeException {

    public JwtDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
