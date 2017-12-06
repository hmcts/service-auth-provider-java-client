package uk.gov.hmcts.authorisation;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@EnableFeignClients
@FeignClient(name = "idam-s2s-auth", url = "${idam.s2s-auth.url}")
public interface ServiceAuthorisationApi {
    @RequestMapping(method = RequestMethod.POST, value = "/lease")
    String serviceToken(@RequestParam("microservice") final String microservice,
                        @RequestParam("oneTimePassword") final String oneTimePassword);
}
