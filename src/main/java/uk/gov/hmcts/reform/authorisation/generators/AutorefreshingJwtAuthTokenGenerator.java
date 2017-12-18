package uk.gov.hmcts.reform.authorisation.generators;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import uk.gov.hmcts.reform.authorisation.exceptions.JWTDecodingException;

import java.util.Date;

import static java.time.Instant.now;

/**
 * Caches the JWT token and refreshes it once it expired.
 */
public class AutorefreshingJwtAuthTokenGenerator implements AuthTokenGenerator {

    private final ServiceAuthTokenGenerator generator;

    private DecodedJWT jwt = null;

    public AutorefreshingJwtAuthTokenGenerator(ServiceAuthTokenGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String generate() {
        if (this.jwt == null || needToRefresh(jwt.getExpiresAt())) {
            String newToken = generator.generate();

            try {
                this.jwt = JWT.decode(newToken);
            } catch (JWTDecodeException exc) {
                throw new JWTDecodingException(exc.getMessage(), exc);
            }
        }

        return jwt.getToken();
    }

    private boolean needToRefresh(Date expDate) {
        return expDate != null && Date.from(now()).after(expDate);
    }
}
