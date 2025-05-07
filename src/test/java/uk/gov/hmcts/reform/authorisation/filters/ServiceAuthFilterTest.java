package uk.gov.hmcts.reform.authorisation.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ServiceAuthFilterTest {

    private static final String AUTH_TOKEN = "Bearer @%%$DFGDFGDF";

    private static final String SERVICE_1 = "service1";

    private static final String SERVICE_2 = "service2";

    private ServiceAuthFilter serviceAuthFilter;

    private AuthTokenValidator authTokenValidator;

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private FilterChain filterChain;

    private final List<String> authorisedServices = Arrays.asList(SERVICE_1, SERVICE_2);


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
    void authorizeValidToken() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(authTokenValidator, times(1)).getServiceName(AUTH_TOKEN);
    }

    @Test
    void failForbiddenAccessServiceAccess() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1 + "fail");
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void failUnAuthorizedServiceAccessIfServiceIsNull() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(null);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void failUnAuthorizedServiceAccessIfServiceException() throws Exception {
        when(authTokenValidator.getServiceName(anyString()))
                .thenThrow(new ServiceException("not reachable", new RuntimeException()));
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void failInvalidBearerToken() throws Exception {
        Mockito.reset(servletRequest);
        when(servletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn(null);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void failIfAuthorisedServicesIsEmpty() {
        List<String> emptyServiceList = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () ->
                new ServiceAuthFilter(authTokenValidator, emptyServiceList)
        );
    }

}