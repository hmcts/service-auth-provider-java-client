package uk.gov.hmcts.reform.authorisation.validators;

import org.junit.Test;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ServiceAuthTokenValidatorTest {

    private final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);
    private final AuthTokenValidator validator = new ServiceAuthTokenValidator(api);
    private static final String SERVICE_AUTH_TOKEN = "service-auth-token";

    @Test
    public void shouldValidateServiceAuthToken() {
        validator.validate(SERVICE_AUTH_TOKEN);

        verify(api, times(1)).authorise(SERVICE_AUTH_TOKEN, new String[0]);
    }

    @Test
    public void shouldRetrieveServiceNameFromS2S() {
        validator.getServiceName(SERVICE_AUTH_TOKEN);

        verify(api, times(1)).getServiceName(SERVICE_AUTH_TOKEN);
    }
}
