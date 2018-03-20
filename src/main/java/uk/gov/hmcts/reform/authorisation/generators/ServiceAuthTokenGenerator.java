package uk.gov.hmcts.reform.authorisation.generators;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.util.MimeTypeUtils;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@SuppressWarnings("SummaryJavadoc")
public class ServiceAuthTokenGenerator implements AuthTokenGenerator {

    private final String secret;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;
    private final GoogleAuthenticator googleAuthenticator;

    public ServiceAuthTokenGenerator(
        final String secret,
        final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this.secret = secret;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    @Override
    public String generate() {
        final String oneTimePassword = format("%06d", googleAuthenticator.getTotpPassword(secret));

        Map<String, String> signInDetails = new HashMap<>();
        signInDetails.put("microservice", this.microService);
        signInDetails.put("oneTimePassword", oneTimePassword);

        return serviceAuthorisationApi.serviceToken(signInDetails, MimeTypeUtils.APPLICATION_JSON_VALUE);
    }
}
