package uk.gov.hmcts.reform.authorisation.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

@Component
public class ServiceAuthHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAuthHealthIndicator.class);

    private final ServiceAuthorisationApi serviceAuthorisationApi;

    @Autowired
    public ServiceAuthHealthIndicator(final ServiceAuthorisationApi serviceAuthorisationApi) {
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    @Override
    public Health health() {
        try {
            InternalHealth internalHealth = this.serviceAuthorisationApi.health();
            return new Health.Builder(internalHealth.getStatus()).build();
        } catch (Exception ex) {
            LOGGER.error("Error on service auth healthcheck", ex);
            return Health.down(ex).build();
        }
    }
}
