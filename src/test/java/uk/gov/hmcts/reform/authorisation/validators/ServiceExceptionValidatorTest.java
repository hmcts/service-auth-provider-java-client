package uk.gov.hmcts.reform.authorisation.validators;

import feign.FeignException;
import feign.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.exceptions.AbstractAuthorisationException;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class ServiceExceptionValidatorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);

    private final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);

    private Class<AbstractAuthorisationException> expectedException;

    private HttpStatus status;

    @Parameterized.Parameters(name = "Testing for HTTP_STATUS {1}")
    public static Iterable<Object[]> data() {
        return Arrays.stream(HttpStatus.values())
                .filter(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError())
                .flatMap(httpStatus -> {
                    Class<?> expected = httpStatus.is4xxClientError()
                        ? InvalidTokenException.class
                        : ServiceException.class;
                    return Arrays.stream(new Object[][]{
                        {expected, httpStatus}
                    });
                })
                .collect(Collectors.toList());
    }

    public ServiceExceptionValidatorTest(Class<AbstractAuthorisationException> expectedException,
                                         HttpStatus status) {
        this.expectedException = expectedException;
        this.status = status;
    }

    @Before
    public void setUp() {
        Response feignResponse = Response.create(status.value(), "i must fail", Collections.emptyMap(), new byte[0]);
        FeignException exception = FeignException.errorStatus("oh no", feignResponse);

        doThrow(exception).when(api).authorise(anyString(), eq(new String[0]));
    }

    @Test
    public void checkExceptions() {
        exception.expect(expectedException);

        validator.validate("some-invalid-token");
    }
}
