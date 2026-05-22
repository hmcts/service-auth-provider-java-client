package uk.gov.hmcts.reform.authorisation;

import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import uk.gov.hmcts.reform.authorisation.config.IntegrationTestInitializer;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("wiremock")
@EnableWireMock({
        @ConfigureWireMock(
                name = "s2s",
                baseUrlProperties = "idam.s2s-auth.url"
        )
})
@SpringBootTest(classes = IntegrationTestInitializer.class)
class ServiceAuthorisationApiTest {
    private static final String DETAILS_ENDPOINT = "/details";
    private static final String DEFAULT_SERVICE = "service";

    @Autowired
    private ServiceAuthorisationApi s2sApi;

    @Autowired
    private ServiceAuthFilter serviceAuthFilter;

    private FilterChain filterChain;

    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void before() {
        WireMock.reset();

        filterChain = spy(FilterChain.class);
        httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn("token");
    }

    @Test
    void shouldGetServiceNameProvidingValidToken() {
        stubFor(get(DETAILS_ENDPOINT).willReturn(ok(DEFAULT_SERVICE)));

        AuthTokenValidator validator = new ServiceAuthTokenValidator(s2sApi);

        assertThat(validator.getServiceName("token")).isEqualTo(DEFAULT_SERVICE);
    }

    @Test
    void shouldPassServiceAuthFilterWithAuthorizedAccess() throws ServletException, IOException {
        stubFor(get(DETAILS_ENDPOINT).willReturn(ok("service1")));

        serviceAuthFilter.doFilter(httpServletRequest, mock(HttpServletResponse.class), filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void shouldFailServiceAuthFilterWithUnauthorizedAccess() throws ServletException, IOException {
        stubFor(get(DETAILS_ENDPOINT).willReturn(status(HttpStatus.GATEWAY_TIMEOUT_504)));

        HttpServletResponse response = mock(HttpServletResponse.class);

        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);

        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED_401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldFailServiceAuthFilterWithForbiddenAccess() throws ServletException, IOException {
        stubFor(get(DETAILS_ENDPOINT).willReturn(ok(DEFAULT_SERVICE)));

        HttpServletResponse response = mock(HttpServletResponse.class);

        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);

        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN_403);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
