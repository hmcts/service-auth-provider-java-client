package uk.gov.hmcts.reform.authorisation.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationHealthApi;

public class ServiceAuthHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAuthHealthIndicator.class);

    private final ServiceAuthorisationHealthApi serviceAuthorisationHealthApi;

    @Autowired
    public ServiceAuthHealthIndicator(final ServiceAuthorisationHealthApi serviceAuthorisationHealthApi) {
        this.serviceAuthorisationHealthApi = serviceAuthorisationHealthApi;
    }

    @Override
    public Health health() {
        try {
            InternalHealth internalHealth = this.serviceAuthorisationHealthApi.health();
            return new Health.Builder(internalHealth.status()).build();
        } catch (Exception ex) {
            LOGGER.error("Error on service auth healthcheck", ex);
            return Health.down(ex).build();
        }
    }
}
