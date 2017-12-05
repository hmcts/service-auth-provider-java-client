package uk.gov.hmcts.authorisation.generators;

import uk.gov.hmcts.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.authorisation.models.ServiceAuthRequest;

public class ServiceAuthTokenGenerator implements AuthTokenGenerator {

    private final String oneTimePassword;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    public ServiceAuthTokenGenerator(
            final String oneTimePassword,
            final String microService,
            final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this.oneTimePassword = oneTimePassword;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    @Override
    public String generate() {
        final ServiceAuthRequest authRequest = new ServiceAuthRequest(this.oneTimePassword, this.microService);
        return serviceAuthorisationApi.serviceToken(authRequest);
    }
}
