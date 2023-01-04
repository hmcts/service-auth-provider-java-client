package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BearerTokenGeneratorTest {

    @Mock
    private AuthTokenGenerator baseGenerator;

    private BearerTokenGenerator bearerTokenGenerator;

    @Before
    public void beforeEachTest() {
        bearerTokenGenerator = new BearerTokenGenerator(baseGenerator);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerWhenInitialisedWithNullGenerator() {
        new BearerTokenGenerator(null);
    }

    @Test
    public void shouldPrependBearerToTokenReturnedFromDecoratedGenerator() {
        String token = "abc123==";
        when(baseGenerator.generate()).thenReturn(token);
        assertThat(bearerTokenGenerator.generate()).isEqualTo(format("Bearer %s", token));
    }

    @Test
    public void shouldNotPrependBearerWhenItIsAlreadyThere() {
        String bearerToken = "Bearer abc123==";
        when(baseGenerator.generate()).thenReturn(bearerToken);
        assertThat(bearerTokenGenerator.generate()).isEqualTo(bearerToken);
    }

}
