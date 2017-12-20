package uk.gov.hmcts.reform.authorisation;

import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.hmcts.reform.authorisation.healthcheck.InternalHealth;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@FeignClient(name = "idam-s2s-auth-health", url = "${idam.s2s-auth.url}",
        configuration = ServiceAuthorisationHealthApi.ServiceAuthConfiguration.class)
public interface ServiceAuthorisationHealthApi {

    @GetMapping(value = "/health", headers = CONTENT_TYPE + "=" + APPLICATION_JSON_UTF8_VALUE)
    InternalHealth health();

    class ServiceAuthConfiguration {
        @Bean
        @Primary
        @Scope("prototype")
        Decoder feignDecoder() {
            return new JacksonDecoder();
        }
    }
}
