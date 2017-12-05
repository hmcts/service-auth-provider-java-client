package uk.gov.hmcts.authorisation.models;

public class ServiceAuthRequest {
    public final String oneTimePassword;
    public final String microservice;

    public ServiceAuthRequest(final String oneTimePassword, final String microService) {
        this.oneTimePassword = oneTimePassword;
        this.microservice = microService;
    }
}
