package uk.gov.hmcts.reform.authorisation.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceAuthFilterTest {

    private static final String AUTH_TOKEN = "Bearer @%%$DFGDFGDF";
    private static final String SERVICE_1 = "service1";
    private static final String SERVICE_2 = "service2";

    private ServiceAuthFilter serviceAuthFilter;
    private AuthTokenValidator authTokenValidator;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private FilterChain filterChain;

    private final List<String> authorisedServices = List.of(SERVICE_1, SERVICE_2);

    @BeforeEach
    void setup() {
        servletRequest = mock(HttpServletRequest.class);
        servletResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        authTokenValidator = mock(AuthTokenValidator.class);
        serviceAuthFilter = new ServiceAuthFilter(authTokenValidator, authorisedServices);

        when(servletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn(AUTH_TOKEN);
    }

    @Test
    void shouldAllowRequestWhenTokenBelongsToAuthorisedService() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1);

        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);

        verify(authTokenValidator).getServiceName(AUTH_TOKEN);
        verify(filterChain).doFilter(servletRequest, servletResponse);
    }

    @Test
    void shouldForbidRequestWhenTokenBelongsToUnauthorisedService() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1 + "fail");

        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setStatus(SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(servletRequest, servletResponse);
    }

    @Test
    void shouldForbidRequestWhenServiceNameIsNull() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(null);

        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setStatus(SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(servletRequest, servletResponse);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenValidationFails() throws Exception {
        when(authTokenValidator.getServiceName(anyString()))
                .thenThrow(new ServiceException("not reachable", new RuntimeException()));

        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setStatus(SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(servletRequest, servletResponse);
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() throws Exception {
        when(servletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn(null);

        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setStatus(SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(servletRequest, servletResponse);
    }

    @Test
    void shouldFailIfAuthorisedServicesIsEmpty() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceAuthFilter(authTokenValidator, List.of())
        );

        assertEquals("Must have at least one service defined", exception.getMessage());
    }
}