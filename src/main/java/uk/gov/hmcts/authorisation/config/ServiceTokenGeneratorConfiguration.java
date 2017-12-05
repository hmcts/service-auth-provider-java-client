package uk.gov.hmcts.authorisation.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.authorisation.generators.CachedServiceAuthTokenGenerator;
import uk.gov.hmcts.authorisation.generators.ServiceAuthTokenGenerator;

@Configuration
@Lazy
@EnableFeignClients(basePackages = "uk.gov.hmcts.authorisation")
public class ServiceTokenGeneratorConfiguration {

    @Bean
    public AuthTokenGenerator serviceAuthTokenGenerator(
            @Value("${idam.s2s-auth.totp_secret}") final String oneTimePassword,
            @Value("${idam.s2s-auth.microservice}") final String microService,
            final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return new ServiceAuthTokenGenerator(oneTimePassword, microService, serviceAuthorisationApi);
    }

    @Bean
    public AuthTokenGenerator cachedServiceAuthTokenGenerator(
            @Qualifier("serviceAuthTokenGenerator") AuthTokenGenerator serviceAuthTokenGenerator,
            @Value("${idam.s2s-auth.tokenTimeToLiveInSeconds:14400}"
            ) int ttl) {
        return new CachedServiceAuthTokenGenerator(serviceAuthTokenGenerator, ttl);
    }
}
