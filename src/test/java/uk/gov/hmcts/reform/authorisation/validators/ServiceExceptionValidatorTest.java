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
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class ServiceExceptionValidatorTest {

    private final ServiceAuthorisationApi api = mock(ServiceAuthorisationApi.class);
    private final ServiceAuthTokenValidator validator = new ServiceAuthTokenValidator(api);

    private HttpStatus status;

    static Stream<HttpStatus> data() {
        return Arrays.stream(HttpStatus.values())
                .filter(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError());
    }

    @ParameterizedTest(name = "Testing for HTTP_STATUS {0}")
    @MethodSource("data")
    void checkExceptions(HttpStatus status) {
        this.status = status;

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

            assertThrows(RuntimeException.class, () -> {
                validator.validate("some-invalid-token");
            });
        }
    }
}
