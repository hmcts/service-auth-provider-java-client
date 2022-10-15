package uk.gov.hmcts.reform.authorisation.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public ServiceAuthFilter(AuthTokenValidator authTokenValidator, List<String> authorisedServices) {

        this.authTokenValidator = authTokenValidator;
        if (authorisedServices == null || authorisedServices.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one service defined");
        }
        this.authorisedServices = authorisedServices.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {

            String bearerToken = extractBearerToken(request);
            String serviceName = authTokenValidator.getServiceName(bearerToken);
            if (!authorisedServices.contains(serviceName)) {
                LOG.debug(
                        "service forbidden {} for endpoint: {} method: {} ",
                        serviceName,
                        request.getRequestURI(),
                        request.getMethod()
                );
                response.setStatus(HttpStatus.FORBIDDEN.value());
            } else {
                LOG.debug(
                        "service authorized {} for endpoint: {} method: {}  ",
                        serviceName,
                        request.getRequestURI(),
                        request.getMethod()
                );
                filterChain.doFilter(request, response);
            }
        } catch (InvalidTokenException | ServiceException exception) {
            LOG.warn("Unsuccessful service authentication", exception);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORISATION);
        if (token == null) {
            throw new InvalidTokenException("ServiceAuthorization Token is missing");
        }
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

}
