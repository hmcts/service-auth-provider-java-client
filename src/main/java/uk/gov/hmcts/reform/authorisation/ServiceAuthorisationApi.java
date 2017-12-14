package uk.gov.hmcts.reform.authorisation;

import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.hmcts.reform.authorisation.healthcheck.InternalHealth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@EnableFeignClients
@FeignClient(name = "idam-s2s-auth", url = "${idam.s2s-auth.url}",
        configuration = ServiceAuthorisationApi.ServiceAuthConfiguration.class)
public interface ServiceAuthorisationApi {
    @PostMapping(value = "/lease")
    String serviceToken(@RequestParam("microservice") final String microservice,
                        @RequestParam("oneTimePassword") final String oneTimePassword);

    @GetMapping(value = "/authorisation-check")
    void authorise(@RequestHeader(AUTHORIZATION) final String authHeader,
                   @RequestParam("role") final String[] roles);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/health",
            headers = CONTENT_TYPE + "=" + APPLICATION_JSON_UTF8_VALUE
    )
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
