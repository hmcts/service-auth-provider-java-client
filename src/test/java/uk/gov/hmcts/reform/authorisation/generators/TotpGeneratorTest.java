package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TotpGeneratorTest {

    private static final String RFC_6238_SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";

    @Test
    public void shouldMatchRfc6238TestVectors() {
        assertThat(TotpGenerator.generate(RFC_6238_SECRET, 59_000L)).isEqualTo("287082");
        assertThat(TotpGenerator.generate(RFC_6238_SECRET, 1_111_111_109_000L)).isEqualTo("081804");
        assertThat(TotpGenerator.generate(RFC_6238_SECRET, 1_111_111_111_000L)).isEqualTo("050471");
        assertThat(TotpGenerator.generate(RFC_6238_SECRET, 1_234_567_890_000L)).isEqualTo("005924");
        assertThat(TotpGenerator.generate(RFC_6238_SECRET, 2_000_000_000_000L)).isEqualTo("279037");
        assertThat(TotpGenerator.generate(RFC_6238_SECRET, 20_000_000_000_000L)).isEqualTo("353130");
    }

    @Test
    public void shouldReproduceGoogleAuthenticatorCodes() {
        assertThat(TotpGenerator.generate("AAAAAAAAAAAAAAAA", 1_700_000_000_000L)).isEqualTo("501315");
        assertThat(TotpGenerator.generate("aaaaaaaaaaaaaaaa", 1_700_000_000_000L)).isEqualTo("501315");
        assertThat(TotpGenerator.generate("MFRGGZDFMZTWQ2LK", 1_700_000_000_000L)).isEqualTo("882247");
        assertThat(TotpGenerator.generate("123456", 1_700_000_000_000L)).isEqualTo("804364");
    }
}
