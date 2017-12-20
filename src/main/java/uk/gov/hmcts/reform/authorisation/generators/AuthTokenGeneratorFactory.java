package uk.gov.hmcts.reform.authorisation.generators;

import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

public class AuthTokenGeneratorFactory {

    public AuthTokenGenerator createDefaultGenerator(
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

}
