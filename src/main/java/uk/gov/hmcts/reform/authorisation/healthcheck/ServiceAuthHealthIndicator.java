package uk.gov.hmcts.reform.authorisation.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationHealthApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

@Component
@ConditionalOnProperty(prefix = "idam.s2s-auth", name = "url")
public class ServiceAuthHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAuthHealthIndicator.class);

    private final ServiceAuthorisationHealthApi serviceAuthorisationHealthApi;
    private final String secret;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;

    @Autowired
    public ServiceAuthHealthIndicator(
            ServiceAuthorisationHealthApi serviceAuthorisationHealthApi,
            @Value("${idam.s2s-auth.totp_secret}") String secret,
            @Value("${idam.s2s-auth.microservice}") String microService,
            ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this.serviceAuthorisationHealthApi = serviceAuthorisationHealthApi;
        this.secret = secret;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    @Override
    public Health health() {
        try {
            InternalHealth internalHealth = this.serviceAuthorisationHealthApi.health();
            if (!internalHealth.getStatus().getCode().equalsIgnoreCase(Status.UP.getCode())) {
                return new Health.Builder(internalHealth.getStatus()).build();
            } else {
                String token = generateToken();
                this.serviceAuthorisationApi.getServiceName(token);
                return Health.up().build();
            }
        } catch (Exception ex) {
            LOGGER.error("Error on service auth healthcheck", ex);
            return Health.down(ex).build();
        }
    }

    private String generateToken() {
        AuthTokenGenerator authTokenGenerator = AuthTokenGeneratorFactory.createDefaultGenerator(
                secret,
                microService,
                serviceAuthorisationApi
        );
        return authTokenGenerator.generate();
    }
}
