package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceAuthTokenGeneratorTest {

    private final ServiceAuthorisationApi serviceAuthorisationApi = mock(ServiceAuthorisationApi.class);

    @Test
    void shouldGenerateServiceAuthToken() {
        String secret = "123456";
        String microService = "microservice";
        String serviceAuthToken = "service-auth-token";

        when(serviceAuthorisationApi.serviceToken(anyMap()))
            .thenReturn(serviceAuthToken);

        ServiceAuthTokenGenerator serviceAuthTokenGenerator = new ServiceAuthTokenGenerator(
            secret,
            microService,
            serviceAuthorisationApi
        );

        String result = serviceAuthTokenGenerator.generate();

        assertEquals(serviceAuthToken, result);
        verify(serviceAuthorisationApi).serviceToken(argThat(payload ->
            microService.equals(payload.get("microservice"))
                && payload.containsKey("oneTimePassword")
                && payload.get("oneTimePassword") != null
                && payload.get("oneTimePassword").matches("\\d{6}")
        ));
    }
}
