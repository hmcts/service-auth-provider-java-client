package uk.gov.hmcts.reform.authorisation.generators;

public interface AuthTokenGenerator {

    /**
     * Request for the service auth token.
     * @return Service auth token for the micro service
     */
    String generate();
}
