package uk.gov.hmcts.reform.authorisation;

import feign.codec.Decoder;
import feign.codec.StringDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE;

@FeignClient(
        name = "idam-s2s-auth",
        url = "${idam.s2s-auth.url}",
        configuration = ServiceAuthorisationApi.Config.class
)
public interface ServiceAuthorisationApi {

    @PostMapping(value = "/lease",
            consumes = APPLICATION_JSON_VALUE,
            produces = TEXT_PLAIN_VALUE)
    String serviceToken(@RequestBody Map<String, String> signIn);

    @SuppressWarnings("PMD.UseVarargs")
    @GetMapping("/authorisation-check")
    void authorise(@RequestHeader(AUTHORIZATION) String authHeader,
                   @RequestParam("role") String[] roles);

    @GetMapping("/details")
    String getServiceName(@RequestHeader(AUTHORIZATION) String authHeader);

    class Config {
        @Bean
        /* default */ Decoder stringDecoder() {
            return new StringDecoder();
        }
    }
}
