package uk.gov.hmcts.authorisation.models;

public class ServiceAuthToken {
    public String bearerToken;

    public ServiceAuthToken(final String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
