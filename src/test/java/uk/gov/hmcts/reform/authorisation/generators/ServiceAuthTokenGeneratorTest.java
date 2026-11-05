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

    @Test
    void shouldSendSha256OtpWhenExplicitlySelected() {
        String secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQGEZA====";
        long before = System.currentTimeMillis();
        new ServiceAuthTokenGenerator(secret, "upgraded_service", serviceAuthorisationApi,
            TotpAlgorithm.HMAC_SHA256).generate();
        long after = System.currentTimeMillis();
        verify(serviceAuthorisationApi).serviceToken(argThat(payload ->
            "upgraded_service".equals(payload.get("microservice"))
                && (TotpGenerator.generate(secret, before, TotpAlgorithm.HMAC_SHA256)
                    .equals(payload.get("oneTimePassword"))
                || TotpGenerator.generate(secret, after, TotpAlgorithm.HMAC_SHA256)
                    .equals(payload.get("oneTimePassword")))
        ));
    }
}
