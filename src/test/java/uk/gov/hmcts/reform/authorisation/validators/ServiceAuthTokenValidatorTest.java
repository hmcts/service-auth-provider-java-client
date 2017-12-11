package uk.gov.hmcts.reform.authorisation.validators;

import feign.FeignException;
import feign.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;

import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ServiceAuthTokenValidatorTest {
    private final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldValidateServiceAuthToken() {
        final String serviceAuthToken = "service-auth-token";
        final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);

        validator.validate(serviceAuthToken);

        verify(api, times(1)).authorise(serviceAuthToken, new String[0]);
    }

    @Test
    public void shouldThrowInvalidTokenException() {
        exception.expect(InvalidTokenException.class);

        throwFeignException(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void shouldThrowServiceException() {
        exception.expect(ServiceException.class);

        throwFeignException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private void throwFeignException(HttpStatus status) {
        Response feignResponse = Response.create(status.value(), "i must fail", Collections.emptyMap(), new byte[0]);
        FeignException exception = FeignException.errorStatus("oh no", feignResponse);

        doThrow(exception).when(api).authorise(anyString(), eq(new String[0]));

        final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);
        validator.validate("service-auth-token");
    }
}
