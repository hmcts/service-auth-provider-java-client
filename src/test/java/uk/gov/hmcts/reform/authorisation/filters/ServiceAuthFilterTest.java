package uk.gov.hmcts.reform.authorisation.filters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.LawOfDemeter")
public class ServiceAuthFilterTest {

    private static final String AUTH_TOKEN = "Bearer @%%$DFGDFGDF";

    private static final String SERVICE_1 = "service1";

    private static final String SERVICE_2 = "service2";

    private ServiceAuthFilter serviceAuthFilter;

    private AuthTokenValidator authTokenValidator;

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private FilterChain filterChain;

    private final List<String> authorisedServices = Arrays.asList(SERVICE_1, SERVICE_2);


    @Before
    public void setup() {
        servletRequest = mock(HttpServletRequest.class);
        servletResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        authTokenValidator = mock(AuthTokenValidator.class);
        serviceAuthFilter = new ServiceAuthFilter(authTokenValidator, authorisedServices);
        when(servletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn(AUTH_TOKEN);
    }

    @Test
    public void authorizeValidToken() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(authTokenValidator, times(1)).getServiceName(AUTH_TOKEN);
    }

    @Test
    public void failForbiddenAccessServiceAccess() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1 + "fail");
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void failUnAuthorizedServiceAccessIfServiceIsNull() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(null);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void failUnAuthorizedServiceAccessIfServiceException() throws Exception {
        when(authTokenValidator.getServiceName(anyString()))
                .thenThrow(new ServiceException("not reachable", new RuntimeException()));
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void failInvalidBearerToken() throws Exception {
        Mockito.reset(servletRequest);
        when(servletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn(null);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failIfAuthorisedServicesIsEmpty() throws Exception {
        serviceAuthFilter = new ServiceAuthFilter(authTokenValidator, new ArrayList<>());
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
    }

}