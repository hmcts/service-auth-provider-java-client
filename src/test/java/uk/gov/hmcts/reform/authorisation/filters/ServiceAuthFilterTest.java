package uk.gov.hmcts.reform.authorisation.filters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class )
public class ServiceAuthFilterTest {

    private static final String SERVICE_1 = "service1";

    private static final String SERVICE_2 = "service2";

    private ServiceAuthFilter serviceAuthFilter;

    private AuthTokenValidator authTokenValidator;

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private FilterChain filterChain;

    private static String AUTH_TOKEN = "Bearer @%%$DFGDFGDF";

    private List<String> authorisedServices = Arrays.asList(SERVICE_1, SERVICE_2);


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
    public void failUnAuthorizedServiceAccess() throws Exception {
        when(authTokenValidator.getServiceName(anyString())).thenReturn(SERVICE_1+"fail");
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(403);
    }

    @Test
    public void failInvalidBearerToken() throws Exception {
        Mockito.reset(servletRequest);
        when(servletRequest.getHeader(ServiceAuthFilter.AUTHORISATION)).thenReturn(null);
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
        verify(servletResponse, times(1)).setStatus(403);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failIfAuthorisedServicesIsEmpty() throws Exception {
        serviceAuthFilter = new ServiceAuthFilter(authTokenValidator, new ArrayList<>());
        serviceAuthFilter.doFilterInternal(servletRequest, servletResponse, filterChain);
    }

}