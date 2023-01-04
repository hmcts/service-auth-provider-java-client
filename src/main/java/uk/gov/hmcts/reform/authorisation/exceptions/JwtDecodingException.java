package uk.gov.hmcts.reform.authorisation.exceptions;

public class JwtDecodingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JwtDecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
