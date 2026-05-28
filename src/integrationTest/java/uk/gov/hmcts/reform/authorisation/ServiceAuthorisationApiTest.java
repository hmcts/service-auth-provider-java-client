package uk.gov.hmcts.reform.authorisation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.spring.ConfigureWireMock;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_GATEWAY_TIMEOUT;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("wiremock")
@ConfigureWireMock(
    name = "s2s",
    port = 0,
    portProperties = "wiremock.server.port",
    baseUrlProperties = "idam.s2s-auth.url"
    )
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
@TestPropertySource(properties = {
    "idam.s2s-authorised.services=service1,service2",
})
@SpringBootTest(classes = ServiceAuthorisationApiTest.class)
class ServiceAuthorisationApiTest {

    private static final String DETAILS_ENDPOINT = "/details";
    private static final String AUTHORISED_SERVICE = "service1";
    private static final String UNAUTHORISED_SERVICE = "service";

    @Autowired
    private ServiceAuthorisationApi s2sApi;

    @Autowired
    private ServiceAuthFilter serviceAuthFilter;

    private FilterChain filterChain;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void before() {
        filterChain = spy(FilterChain.class);
        httpServletRequest = mock(HttpServletRequest.class);

        when(httpServletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn("token");
    }

    @Test
    void shouldGetServiceNameProvidingValidToken() {
        AuthTokenValidator validator = new ServiceAuthTokenValidator(s2sApi);

        givenThat(get(DETAILS_ENDPOINT)
                .willReturn(status(SC_OK).withBody(UNAUTHORISED_SERVICE)));

        assertEquals(UNAUTHORISED_SERVICE, validator.getServiceName("token"));
    }

    @Test
    void shouldPassServiceAuthFilterWithAuthorizedAccess() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT)
                .willReturn(status(SC_OK).withBody(AUTHORISED_SERVICE)));

        serviceAuthFilter.doFilter(httpServletRequest, mock(HttpServletResponse.class), filterChain);

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldFailServiceAuthFilterWithUnauthorizedAccess() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT)
                .willReturn(status(SC_GATEWAY_TIMEOUT)));

        HttpServletResponse response = mock(HttpServletResponse.class);

        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);

        verify(response).setStatus(SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldFailServiceAuthFilterWithForbiddenAccess() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT)
                .willReturn(status(SC_OK).withBody(UNAUTHORISED_SERVICE)));

        HttpServletResponse response = mock(HttpServletResponse.class);

        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);

        verify(response).setStatus(SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
