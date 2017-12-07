package uk.gov.hmcts.reform.authorisation.generators;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static java.lang.String.format;

public class ServiceAuthTokenGenerator implements AuthTokenGenerator {

    private final String secret;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;
    private final GoogleAuthenticator googleAuthenticator;

    public ServiceAuthTokenGenerator(
            final String secret,
            final String microService,
            final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this.secret = secret;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    /***
     *
     * @return Service Auth token for the micro service
     */
    @Override
    public String generate() {
        final String oneTimePassword = format("%06d", googleAuthenticator.getTotpPassword(secret));
        return serviceAuthorisationApi.serviceToken(this.microService, oneTimePassword);
    }
}
