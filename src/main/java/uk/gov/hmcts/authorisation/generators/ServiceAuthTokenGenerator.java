package uk.gov.hmcts.authorisation.generators;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import uk.gov.hmcts.authorisation.ServiceAuthorisationApi;

import static java.lang.Integer.valueOf;

public class ServiceAuthTokenGenerator implements AuthTokenGenerator {

    private final String secret;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    public ServiceAuthTokenGenerator(
            final String secret,
            final String microService,
            final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this.secret = secret;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    @Override
    public String generate() {
        final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        final String oneTimePassword = valueOf(googleAuthenticator.getTotpPassword(secret)).toString();
        return serviceAuthorisationApi.serviceToken(this.microService, oneTimePassword);
    }
}
