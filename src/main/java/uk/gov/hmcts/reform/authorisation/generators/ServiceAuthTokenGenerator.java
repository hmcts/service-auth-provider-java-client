package uk.gov.hmcts.reform.authorisation.generators;

import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.util.Map;

@SuppressWarnings("SummaryJavadoc")
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
        final String oneTimePassword = TotpGenerator.generate(secret);

        Map<String, String> signInDetails = Map.of(
                "microservice", this.microService,
                "oneTimePassword", oneTimePassword
        );

        return serviceAuthorisationApi.serviceToken(signInDetails);
    }
}
