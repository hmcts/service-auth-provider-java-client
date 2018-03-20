package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;

public class ServiceAuthTokenGeneratorTest {
    private final ServiceAuthorisationApi serviceAuthorisationApi = Mockito.mock(ServiceAuthorisationApi.class);

    @Test
    public void shouldGenerateServiceAuthToken() {
        //given
        final String secret = "123456";
        final String microService = "microservice";
        final String serviceAuthToken = "service-auth-token";

        when(serviceAuthorisationApi.serviceToken(anyMapOf(String.class, String.class)))
            .thenReturn(serviceAuthToken);

        //when
        final ServiceAuthTokenGenerator serviceAuthTokenGenerator = new ServiceAuthTokenGenerator(secret,
            microService, serviceAuthorisationApi);

        final String result = serviceAuthTokenGenerator.generate();

        //then
        assertThat(result).isNotNull().isEqualTo(serviceAuthToken);
    }
}
