package uk.gov.hmcts.reform.authorisation.validators;

import org.junit.Test;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ServiceAuthTokenValidatorTest {

    @Test
    public void shouldValidateServiceAuthToken() {
        final String serviceAuthToken = "service-auth-token";
        final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);
        final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);

        validator.validate(serviceAuthToken);

        verify(api, times(1)).authorise(serviceAuthToken, new String[0]);
    }
}
