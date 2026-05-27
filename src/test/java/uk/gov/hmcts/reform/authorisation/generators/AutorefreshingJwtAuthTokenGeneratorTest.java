package uk.gov.hmcts.reform.authorisation.generators;

import com.auth0.jwt.JWT;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.authorisation.exceptions.JwtDecodingException;

import java.time.Instant;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static java.time.Duration.of;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AutorefreshingJwtAuthTokenGeneratorTest {

    private final ServiceAuthTokenGenerator generator = mock(ServiceAuthTokenGenerator.class);

    private final AutorefreshingJwtAuthTokenGenerator jwtAuthTokenGenerator =
        new AutorefreshingJwtAuthTokenGenerator(generator);

    @Test
    void shouldRequestNewTokenFromPassedGeneratorOnFirstUsage() {
        String tokenFromS2S = jwtTokenWithExpDate(now());

        given(generator.generate()).willReturn(tokenFromS2S);

        String token = jwtAuthTokenGenerator.generate();

        assertEquals(tokenFromS2S, token);
    }

    @Test
    void shouldNotRequestNewTokenIfCachedTokenIsStillValid() {
        given(generator.generate())
            .willReturn(jwtTokenWithExpDate(now().plus(2, HOURS)));

        repeat(5, jwtAuthTokenGenerator::generate);

        verify(generator).generate();
    }

    @Test
    void shouldRequestNewTokenOnceItExpires() {
        given(generator.generate())
            .willReturn(jwtTokenWithExpDate(now().minus(2, HOURS)));

        repeat(3, jwtAuthTokenGenerator::generate);

        verify(generator, times(3)).generate();
    }

    @Test
    void shouldThrowAnExceptionIfS2sTokenIsNotAJwtToken() {
        given(generator.generate())
            .willReturn("clearly not a valid JWT token");

        assertThrows(JwtDecodingException.class, jwtAuthTokenGenerator::generate);
    }

    @Test
    void shouldRequestNewTokenIfDeltaIsLargerThanTimeLeftToExpiryDate() {
        given(generator.generate())
            .willReturn(jwtTokenWithExpDate(now().plus(1, MINUTES)));

        AutorefreshingJwtAuthTokenGenerator jwtGenerator =
            new AutorefreshingJwtAuthTokenGenerator(generator, of(2, MINUTES));

        repeat(2, jwtGenerator::generate);

        verify(generator, times(2)).generate();
    }

    private String jwtTokenWithExpDate(Instant expAtDate) {
        return JWT.create()
            .withExpiresAt(Date.from(expAtDate))
            .sign(HMAC256("secret"));
    }

    private void repeat(int times, Runnable action) {
        range(0, times).forEach(i -> action.run());
    }
}
