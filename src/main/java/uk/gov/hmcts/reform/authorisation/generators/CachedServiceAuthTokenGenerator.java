package uk.gov.hmcts.reform.authorisation.generators;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.google.common.base.Suppliers.memoizeWithExpiration;

public class CachedServiceAuthTokenGenerator implements AuthTokenGenerator {
    private final Supplier<String> cachedSupplier;

    public CachedServiceAuthTokenGenerator(
            final AuthTokenGenerator authTokenGenerator,
            final int ttlInSeconds
    ) {
        cachedSupplier = memoizeWithExpiration(authTokenGenerator::generate, ttlInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String generate() {
        return cachedSupplier.get();
    }
}
