package uk.gov.hmcts.reform.authorisation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.authorisation.config.IntegrationTestInitializer;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ActiveProfiles("wiremock")
@AutoConfigureWireMock(port = 0)
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties()
@TestPropertySource(properties = {
    "idam.s2s-authorised.services=service1,service1",
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = IntegrationTestInitializer.class)
public class ServiceAuthorisationApiTest {

    private static final String DETAILS_ENDPOINT = "/details";
    private static final String DEFAULT_SERVICE = "service";
    @Autowired
    private ServiceAuthorisationApi s2sApi;

    @Autowired
    private ServiceAuthFilter serviceAuthFilter;

    private FilterChain filterChain;

    private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void setup() {
        filterChain = spy(FilterChain.class);
        httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn("token");
    }

    @Test
    public void shouldGetServiceNameProvidingValidToken() {
        AuthTokenValidator validator = new ServiceAuthTokenValidator(s2sApi);
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withBody(DEFAULT_SERVICE)));
        assertThat(validator.getServiceName("token")).isEqualTo(DEFAULT_SERVICE);
    }

    @Test
    public void shouldPassServiceAuthFilterWithAuthorizedAccess() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withBody("service1")));
        serviceAuthFilter.doFilter(httpServletRequest, mock(HttpServletResponse.class), filterChain);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void shouldFailServiceAuthFilterWithUnauthorizedAccess() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withStatus(HttpStatus.GATEWAY_TIMEOUT_504)));
        HttpServletResponse response = mock(HttpServletResponse.class);
        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);
        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED_401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void shouldFailServiceAuthFilterWithForbiddenAccess() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withBody(DEFAULT_SERVICE)));
        HttpServletResponse response = mock(HttpServletResponse.class);
        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN_403);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
