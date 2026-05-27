package uk.gov.hmcts.reform.authorisation.generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerTokenGeneratorTest {

    @Mock
    private AuthTokenGenerator baseGenerator;

    private BearerTokenGenerator bearerTokenGenerator;

    @BeforeEach
    void beforeEachTest() {
        bearerTokenGenerator = new BearerTokenGenerator(baseGenerator);
    }

    @Test
    void shouldThrowNullPointerWhenInitialisedWithNullGenerator() {
        assertThrows(
            NullPointerException.class,
            () -> new BearerTokenGenerator(null)
        );
    }

    @Test
    void shouldPrependBearerToTokenReturnedFromDecoratedGenerator() {
        String token = "abc123==";

        when(baseGenerator.generate()).thenReturn(token);

        assertEquals("Bearer " + token, bearerTokenGenerator.generate());
    }

    @Test
    void shouldNotPrependBearerWhenItIsAlreadyThere() {
        String bearerToken = "Bearer abc123==";

        when(baseGenerator.generate()).thenReturn(bearerToken);

        assertEquals(bearerToken, bearerTokenGenerator.generate());
    }
}
