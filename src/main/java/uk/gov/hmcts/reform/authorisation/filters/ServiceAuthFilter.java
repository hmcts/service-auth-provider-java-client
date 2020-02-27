package uk.gov.hmcts.reform.authorisation.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceAuthFilter extends OncePerRequestFilter {

    public static final String AUTHORISATION = "ServiceAuthorization";
    private static final Logger LOG = LoggerFactory.getLogger(ServiceAuthFilter.class);
    private final List<String> authorisedServices;

    private final AuthTokenValidator authTokenValidator;

    @Autowired
    public ServiceAuthFilter(AuthTokenValidator authTokenValidator,
                             @Value("${idam.s2s-auth.url}") List<String> authorisedServices) {

        this.authTokenValidator = authTokenValidator;
        if (authorisedServices.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one service defined");
        }
        this.authorisedServices = authorisedServices.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String serviceName = authorise(request);
            LOG.debug("service authorized {}", serviceName);

        } catch (RuntimeException ex) {
            LOG.warn("Unsuccessful service authentication", ex);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String authorise(HttpServletRequest request) {
        String bearerToken = extractBearerToken(request);
        String serviceName;
        try {
            serviceName = authTokenValidator.getServiceName(bearerToken);
        } catch (InvalidTokenException | ServiceException ex) {
            throw new InvalidTokenException(ex.getMessage(), ex.getCause());
        }
        if (!authorisedServices.contains(serviceName)) {
            throw new InvalidTokenException("Unauthorised service access");
        }
        return serviceName;
    }

    private String extractBearerToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORISATION);
        if (token == null) {
            throw new InvalidTokenException("ServiceAuthorization Token is missing");
        }
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }


}
