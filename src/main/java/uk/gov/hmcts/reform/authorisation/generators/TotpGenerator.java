package uk.gov.hmcts.reform.authorisation.generators;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Locale;
import javax.crypto.spec.SecretKeySpec;

final class TotpGenerator {

    private static final TimeBasedOneTimePasswordGenerator TOTP = new TimeBasedOneTimePasswordGenerator();

    private TotpGenerator() {
    }

    static String generate(String base32Secret) {
        return generate(base32Secret, System.currentTimeMillis());
    }

    static String generate(String base32Secret, long epochMilli) {
        byte[] key = new Base32().decode(base32Secret.toUpperCase(Locale.ROOT));

        try {
            return TOTP.generateOneTimePasswordString(
                new SecretKeySpec(key, "HmacSHA1"), Instant.ofEpochMilli(epochMilli), Locale.ROOT
            );
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Failed to generate one time password", e);
        }
    }
}
