package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CachedServiceAuthTokenGeneratorTest {

    private final AuthTokenGenerator delegate = Mockito.mock(AuthTokenGenerator.class);

    @Test
    public void shouldReuserOldTokenIfTimeHasNotExpired() {
        CachedServiceAuthTokenGenerator generator = new CachedServiceAuthTokenGenerator(delegate, 900);
        generator.generate();
        generator.generate();
        generator.generate();
        verify(delegate, times(1)).generate();
    }

    @Test
    public void shouldMakeAnotherCallIfSpecifiedTimeHasPassed() throws InterruptedException {
        CachedServiceAuthTokenGenerator generator = new CachedServiceAuthTokenGenerator(delegate, 1);
        generator.generate();
        verify(delegate, times(1)).generate();
        Thread.sleep(1000);    // no easy way to fake the Clock for Suppliers.memoizeWithExpiration
        generator.generate();
        verify(delegate, times(2)).generate();
    }

}
