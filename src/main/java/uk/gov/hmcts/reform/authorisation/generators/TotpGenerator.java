package uk.gov.hmcts.reform.authorisation.generators;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.commons.codec.binary.Base32;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

final class TotpGenerator {

    private static final Map<TotpAlgorithm, TimeBasedOneTimePasswordGenerator> GENERATORS = Map.of(
        TotpAlgorithm.HMAC_SHA1, new TimeBasedOneTimePasswordGenerator(),
        TotpAlgorithm.HMAC_SHA256, sha256Generator()
    );

    private static TimeBasedOneTimePasswordGenerator sha256Generator() {
        try {
            return new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30), 6, "HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("HmacSHA256 is not available", e);
        }
    }

    private TotpGenerator() {
    }

    static String generate(String base32Secret) {
        return generate(base32Secret, System.currentTimeMillis());
    }

    static String generate(String base32Secret, long epochMilli) {
        return generate(base32Secret, epochMilli, TotpAlgorithm.HMAC_SHA1);
    }

    static String generate(String base32Secret, TotpAlgorithm algorithm) {
        return generate(base32Secret, System.currentTimeMillis(), algorithm);
    }

    static String generate(String base32Secret, long epochMilli, TotpAlgorithm algorithm) {
        byte[] key = new Base32().decode(base32Secret.toUpperCase(Locale.ROOT));

        try {
            return GENERATORS.get(algorithm).generateOneTimePasswordString(
                new SecretKeySpec(key, algorithm.macAlgorithm()), Instant.ofEpochMilli(epochMilli), Locale.ROOT
            );
        } catch (InvalidKeyException e) {
            throw new IllegalStateException("Failed to generate one time password", e);
        }
    }
}
