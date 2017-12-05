package uk.gov.hmcts.authorisation;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.authorisation.models.ServiceAuthRequest;

@FeignClient(name = "idam-s2s-auth",
        url = "${idam.s2s-auth.url}")
public interface ServiceAuthorisationApi {
    @RequestMapping(method = RequestMethod.GET, value = "/lease")
    String serviceToken(@RequestBody final ServiceAuthRequest serviceAuthRequest);
}
