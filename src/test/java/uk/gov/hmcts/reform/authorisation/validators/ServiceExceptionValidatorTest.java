package uk.gov.hmcts.reform.authorisation.validators;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class ServiceExceptionValidatorTest {

    private final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);
    private final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);
    private static final String INVALID_TOKEN = "some-invalid-token";
    
    static Stream<HttpStatus> errorStatuses() {
        return Arrays.stream(HttpStatus.values())
                .filter(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError());
    }

    @ParameterizedTest(name = "Testing for HTTP_STATUS {0}")
    @MethodSource("errorStatuses")
    void checkExceptions(HttpStatus status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/test",
                Map.of(),
                Request.Body.empty(),
                new RequestTemplate()
        );

        try (Response feignResponse = Response.builder()
                .request(request)
                .status(status.value())
                .body(new byte[0])
                .reason("i must fail")
                .headers(Map.of())
                .build()) {

            FeignException exception = FeignException.errorStatus("oh no", feignResponse);

            doThrow(exception)
                    .when(api)
                    .authorise(anyString(), eq(new String[0]));

            assertThrows(
                    RuntimeException.class,
                    () -> validator.validate(INVALID_TOKEN)
            );
        }
    }
}
