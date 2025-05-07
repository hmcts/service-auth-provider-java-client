package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

class ServiceAuthTokenGeneratorTest {
    private final ServiceAuthorisationApi serviceAuthorisationApi = Mockito.mock(ServiceAuthorisationApi.class);

    @Test
    void shouldGenerateServiceAuthToken() {
        //given
        final String secret = "123456";
        final String microService = "microservice";
        final String serviceAuthToken = "service-auth-token";

        when(serviceAuthorisationApi.serviceToken(anyMap()))
            .thenReturn(serviceAuthToken);

        //when
        final ServiceAuthTokenGenerator serviceAuthTokenGenerator = new ServiceAuthTokenGenerator(secret,
            microService, serviceAuthorisationApi);

        final String result = serviceAuthTokenGenerator.generate();

        //then
        assertThat(result).isEqualTo(serviceAuthToken);
    }
}
