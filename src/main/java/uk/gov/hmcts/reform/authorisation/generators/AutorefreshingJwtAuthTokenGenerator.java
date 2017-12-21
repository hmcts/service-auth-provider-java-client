package uk.gov.hmcts.reform.authorisation.generators;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import uk.gov.hmcts.reform.authorisation.exceptions.JwtDecodingException;

import java.time.Duration;
import java.util.Date;

import static java.time.Instant.now;

/**
 * Caches the JWT token and refreshes it once it expired.
 */
public class AutorefreshingJwtAuthTokenGenerator implements AuthTokenGenerator {

    private final AuthTokenGenerator generator;
    private final Duration refreshTimeDelta;

    private DecodedJWT jwt;

    /**
     * Constructor.
     *
     * @param generator Token generator for automatic refresh
     * @param refreshTimeDelta Time before actual expiry date in JWT when a new token should be requested.
     */
    public AutorefreshingJwtAuthTokenGenerator(
        AuthTokenGenerator generator,
        Duration refreshTimeDelta
    ) {
        this.generator = generator;
        this.refreshTimeDelta = refreshTimeDelta;
    }

    public AutorefreshingJwtAuthTokenGenerator(AuthTokenGenerator generator) {
        this(generator, Duration.ZERO);
    }

    @Override
    public String generate() {
        if (jwt == null || needToRefresh(jwt.getExpiresAt())) {
            String newToken = generator.generate();

            try {
                jwt = JWT.decode(newToken);
            } catch (JWTDecodeException exc) {
                throw new JwtDecodingException(exc.getMessage(), exc);
            }
        }

        return jwt.getToken();
    }

    private boolean needToRefresh(Date expDate) {
        return expDate != null && Date.from(now().plus(refreshTimeDelta)).after(expDate);
    }
}
