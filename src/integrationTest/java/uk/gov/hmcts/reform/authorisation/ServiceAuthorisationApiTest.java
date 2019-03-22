package uk.gov.hmcts.reform.authorisation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.authorisation.config.IntegrationTestInitializer;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("wiremock")
@AutoConfigureWireMock
@ContextConfiguration(initializers = IntegrationTestInitializer.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceAuthorisationApiTest {

    @Autowired
    private ServiceAuthorisationApi s2sApi;

    @Test
    public void should_get_service_name_providing_valid_token() {
        AuthTokenValidator validator = new ServiceAuthTokenValidator(s2sApi);
        givenThat(get("/details").willReturn(status(OK.value()).withBody("service")));

        assertThat(validator.getServiceName("token")).isEqualTo("service");
    }
}
