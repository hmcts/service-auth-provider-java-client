package uk.gov.hmcts.reform.authorisation.generators;

import static java.util.Objects.requireNonNull;

/**
 * A {@link AuthTokenGenerator AuthTokenGenerator} decorator which wraps the token into a Bearer form, i.e.
 * <code>Bearer &lt;token&gt;</code>.
 */
public class BearerTokenGenerator implements AuthTokenGenerator {

    private AuthTokenGenerator decoreated;

    public BearerTokenGenerator(AuthTokenGenerator decorated) {
        this.decoreated = requireNonNull(decorated);
    }

    @Override
    public String generate() {
        String token = decoreated.generate();
        if (token.matches("Bearer .+")) {
            return token;
        } else {
            return "Bearer " + token;
        }
    }

}
