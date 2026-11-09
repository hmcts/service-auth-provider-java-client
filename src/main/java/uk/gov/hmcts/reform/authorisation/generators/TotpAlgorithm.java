package uk.gov.hmcts.reform.authorisation.generators;

/** Algorithm must match the S2S server configuration for the service identity. */
public enum TotpAlgorithm {
    HMAC_SHA1("HmacSHA1"),
    HMAC_SHA256("HmacSHA256");

    private final String macAlgorithm;

    TotpAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    String macAlgorithm() {
        return macAlgorithm;
    }
}
