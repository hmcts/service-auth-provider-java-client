package uk.gov.hmcts.reform.authorisation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.healthcheck.ServiceAuthHealthIndicator;

@Configuration
@ConditionalOnProperty(prefix = "idam.s2s-auth", name = "url")
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class ServiceAuthAutoConfiguration {

    @Bean
    public ServiceAuthHealthIndicator serviceAuthHealthIndicator(ServiceAuthorisationHealthApi coreCaseDataApi) {
        return new ServiceAuthHealthIndicator(coreCaseDataApi);
    }

}
