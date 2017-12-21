package uk.gov.hmcts.reform.authorisation.generators;

import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.time.Duration;

public final class AuthTokenGeneratorFactory {

    public static AuthTokenGenerator createDefaultGenerator(
            String secret,
            String microService,
            ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return new BearerTokenGenerator(
                new AutorefreshingJwtAuthTokenGenerator(
                        new ServiceAuthTokenGenerator(secret, microService, serviceAuthorisationApi)
                )
        );
    }

    public static AuthTokenGenerator createDefaultGenerator(
            String secret,
            String microService,
            ServiceAuthorisationApi serviceAuthorisationApi,
            Duration refreshTimeDelta
    ) {
        return new BearerTokenGenerator(
                new AutorefreshingJwtAuthTokenGenerator(
                        new ServiceAuthTokenGenerator(secret, microService, serviceAuthorisationApi),
                        refreshTimeDelta
                )
        );
    }

    private AuthTokenGeneratorFactory() {
        // Static factory class
    }

}
