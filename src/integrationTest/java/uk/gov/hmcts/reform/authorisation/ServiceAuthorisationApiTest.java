package uk.gov.hmcts.reform.authorisation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegrationTestInitializer.class)
@SuppressWarnings("PMD.ExcessiveImports")
public class ServiceAuthorisationApiTest {

    private static final String DETAILS_ENDPOINT = "/details";
    private static final String DEFAULT_SERVICE = "service";
    @Autowired
    private ServiceAuthorisationApi s2sApi;

    @Autowired
    private ServiceAuthFilter serviceAuthFilter;

    private FilterChain filterChain;

    private HttpServletRequest httpServletRequest;

    @Before
    public void before() {
        filterChain = spy(FilterChain.class);
        httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn("token");
    }

    @Test
    public void should_get_service_name_providing_valid_token() {
        AuthTokenValidator validator = new ServiceAuthTokenValidator(s2sApi);
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withBody(DEFAULT_SERVICE)));
        assertThat(validator.getServiceName("token")).isEqualTo(DEFAULT_SERVICE);
    }

    @Test
    public void should_pass_serviceAuthFilter_with_authorized_access() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withBody("service1")));
        serviceAuthFilter.doFilter(httpServletRequest, mock(HttpServletResponse.class), filterChain);
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    public void should_fail_serviceAuthFilter_with_Unauthorized_access() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withStatus(HttpStatus.GATEWAY_TIMEOUT_504)));
        HttpServletResponse response = mock(HttpServletResponse.class);
        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);
        verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED_401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void should_fail_serviceAuthFilter_with_Forbidden_access() throws ServletException, IOException {
        givenThat(get(DETAILS_ENDPOINT).willReturn(status(OK.value()).withBody(DEFAULT_SERVICE)));
        HttpServletResponse response = mock(HttpServletResponse.class);
        serviceAuthFilter.doFilter(httpServletRequest, response, filterChain);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN_403);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
