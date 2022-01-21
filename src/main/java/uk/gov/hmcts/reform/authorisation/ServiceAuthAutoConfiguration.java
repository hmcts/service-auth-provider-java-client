package uk.gov.hmcts.reform.authorisation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.authorisation.healthcheck.ServiceAuthHealthIndicator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "idam.s2s-auth", name = "url")
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class ServiceAuthAutoConfiguration {

    @Bean
    @ConditionalOnEnabledHealthIndicator("serviceAuth")
    public ServiceAuthHealthIndicator serviceAuthHealthIndicator(ServiceAuthorisationHealthApi coreCaseDataApi) {
        return new ServiceAuthHealthIndicator(coreCaseDataApi);
    }

    @Bean
    @ConditionalOnProperty("idam.s2s-authorised.services")
    public ServiceAuthFilter serviceAuthFiler(ServiceAuthorisationApi authorisationApi,
                                      @Value("${idam.s2s-authorised.services}") List<String> authorisedServices) {

        AuthTokenValidator authTokenValidator = new ServiceAuthTokenValidator(authorisationApi);
        return new ServiceAuthFilter(authTokenValidator, authorisedServices);

    }

    @Bean
    @ConditionalOnProperty("idam.s2s-authorised.services")
    public FilterRegistrationBean deRegisterServiceAuthFilter(ServiceAuthFilter serviceAuthFilter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(serviceAuthFilter);
        filterRegistrationBean.setEnabled(false);
        return  filterRegistrationBean;
    }

}
