package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotpGeneratorTest {

    private static final String RFC_6238_SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";

    @ParameterizedTest
    @CsvSource({
        "59,           287082",
        "1111111109,   081804",
        "1111111111,   050471",
        "1234567890,   005924",
        "2000000000,   279037",
        "20000000000,  353130"
    })
    void shouldMatchRfc6238TestVectors(long epochSecond, String expected) {
        assertEquals(expected, TotpGenerator.generate(RFC_6238_SECRET, epochSecond * 1000L));
    }

    @ParameterizedTest
    @CsvSource({
        "AAAAAAAAAAAAAAAA,  501315",
        "aaaaaaaaaaaaaaaa,  501315",
        "MFRGGZDFMZTWQ2LK,  882247",
        "123456,            804364"
    })
    void shouldReproduceGoogleAuthenticatorCodes(String secret, String expected) {
        assertEquals(expected, TotpGenerator.generate(secret, 1_700_000_000_000L));
    }

    @ParameterizedTest
    @CsvSource({
        "59,           119246",
        "1111111109,   084774",
        "1111111111,   062674",
        "1234567890,   819424",
        "2000000000,   698825",
        "20000000000,  737706"
    })
    void shouldMatchRfc6238Sha256Vectors(long epochSecond, String expected) {
        String secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQGEZA====";
        assertEquals(expected, TotpGenerator.generate(secret, epochSecond * 1000L, TotpAlgorithm.HMAC_SHA256));
    }
}
