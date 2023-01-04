package uk.gov.hmcts.reform.authorisation.validators;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class ServiceExceptionValidatorTest {

    private final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);

    private final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);

    private final HttpStatus status;

    @Parameterized.Parameters(name = "Testing for HTTP_STATUS {1}")
    public static Iterable<Object[]> data() {
        return Arrays.stream(HttpStatus.values())
                .filter(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError())
                .flatMap(httpStatus -> Arrays.stream(new Object[][]{
                        {httpStatus}
                }))
                .collect(Collectors.toList());
    }

    public ServiceExceptionValidatorTest(HttpStatus status) {
        this.status = status;
    }

    @Before
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.DataflowAnomalyAnalysis"})
    public void setUp() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/test",
                Collections.emptyMap(),
                Request.Body.empty(),
                new RequestTemplate()
        );
        try (Response feignResponse = Response
                .builder()
                .request(request)
                .status(status.value())
                .body(new byte[0])
                .reason("i must fail")
                .headers(Collections.emptyMap())
                .build()) {
            FeignException exception = FeignException.errorStatus("oh no", feignResponse);
            doThrow(exception).when(api).authorise(anyString(), eq(new String[0]));
        }
    }

    @Test(expected =  RuntimeException.class)
    public void checkExceptions() {
        validator.validate("some-invalid-token");
    }
}
