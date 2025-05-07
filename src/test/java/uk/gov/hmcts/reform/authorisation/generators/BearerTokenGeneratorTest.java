package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerTokenGeneratorTest {

    @Mock
    private AuthTokenGenerator baseGenerator;

    private BearerTokenGenerator bearerTokenGenerator;

    @BeforeEach
    void setup() {
        bearerTokenGenerator = new BearerTokenGenerator(baseGenerator);
    }

    @Test
    void shouldThrowNullPointerWhenInitialisedWithNullGenerator() {
        assertThrows(NullPointerException.class, () -> new BearerTokenGenerator(null));
    }

    @Test
    void shouldPrependBearerToTokenReturnedFromDecoratedGenerator() {
        String token = "abc123==";
        when(baseGenerator.generate()).thenReturn(token);
        assertThat(bearerTokenGenerator.generate()).isEqualTo(format("Bearer %s", token));
    }

    @Test
    void shouldNotPrependBearerWhenItIsAlreadyThere() {
        String bearerToken = "Bearer abc123==";
        when(baseGenerator.generate()).thenReturn(bearerToken);
        assertThat(bearerTokenGenerator.generate()).isEqualTo(bearerToken);
    }

}
