package uk.gov.hmcts.reform.authorisation.generators;

import static java.util.Objects.requireNonNull;

/**
 * A {@link AuthTokenGenerator AuthTokenGenerator} decorator which wraps the token into a Bearer form, i.e.
 * <code>Bearer &lt;token&gt;</code>.
 */
public class BearerTokenGenerator implements AuthTokenGenerator {

    private final AuthTokenGenerator decorated;

    public BearerTokenGenerator(AuthTokenGenerator decorated) {
        this.decorated = requireNonNull(decorated);
    }

    @Override
    public String generate() {
        String token = decorated.generate();
        if (token.matches("^Bearer .+")) {
            return token;
        } else {
            return "Bearer " + token;
        }
    }

}
