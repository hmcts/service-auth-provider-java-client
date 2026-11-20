package uk.gov.hmcts.reform.authorisation.generators;

import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.util.Map;

@SuppressWarnings("SummaryJavadoc")
public class ServiceAuthTokenGenerator implements AuthTokenGenerator {

    private final String secret;
    private final TotpAlgorithm algorithm;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    public ServiceAuthTokenGenerator(
        final String secret,
        final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this(secret, microService, serviceAuthorisationApi, TotpAlgorithm.HMAC_SHA1);
    }

    public ServiceAuthTokenGenerator(
        final String secret,
        final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi,
        final TotpAlgorithm algorithm
    ) {
        this.secret = secret;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
        this.algorithm = java.util.Objects.requireNonNull(algorithm);
    }

    @Override
    public String generate() {
        final String oneTimePassword = TotpGenerator.generate(secret, algorithm);

        Map<String, String> signInDetails = Map.of(
                "microservice", this.microService,
                "oneTimePassword", oneTimePassword
        );

        return serviceAuthorisationApi.serviceToken(signInDetails);
    }
}
