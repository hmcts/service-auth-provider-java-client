package uk.gov.hmcts.reform.authorisation.generators;

import com.auth0.jwt.JWT;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.authorisation.exceptions.JwtDecodingException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AutorefreshingJwtAuthTokenGeneratorTest {

    @Mock private ServiceAuthTokenGenerator generator;

    private AutorefreshingJwtAuthTokenGenerator jwtAuthTokenGenerator;

    @Before
    public void setUp() {
        this.jwtAuthTokenGenerator = new AutorefreshingJwtAuthTokenGenerator(generator);
    }

    @Test
    public void should_request_new_token_on_from_passed_generator_on_first_usage() throws Exception {
        // given
        String tokenFromS2S = jwtTokenWithExpDate(now());

        given(generator.generate())
            .willReturn(tokenFromS2S);

        // when
        String token = jwtAuthTokenGenerator.generate();

        // then
        assertThat(token).isEqualTo(tokenFromS2S);
    }

    @Test
    public void should_not_request_new_token_if_cached_token_is_still_valid() throws Exception {
        // given
        given(generator.generate())
            .willReturn(jwtTokenWithExpDate(now().plus(2, HOURS)));

        // when
        repeat(5, () -> jwtAuthTokenGenerator.generate());

        // then
        verify(generator, times(1)).generate();
    }

    @Test
    public void should_request_new_token_once_it_expires() throws Exception {
        // given
        given(generator.generate())
            .willReturn(jwtTokenWithExpDate(now().minus(2, HOURS)));

        // when
        repeat(3, () -> jwtAuthTokenGenerator.generate());

        // then
        verify(generator, times(3)).generate();
    }

    @Test
    public void should_throw_an_exception_if_s2s_token_is_not_a_jwt_token() {
        // given
        given(generator.generate())
            .willReturn("clearly not a valid JWT token");

        // when
        Throwable exc = catchThrowable(() -> jwtAuthTokenGenerator.generate());

        // then
        assertThat(exc)
            .isNotNull()
            .isInstanceOf(JwtDecodingException.class);
    }

    @Test
    public void should_request_a_new_token_if_delta_is_larger_than_time_left_to_expiry_date() throws Exception {
        // given
        // retrieved token is valid for one more minute
        given(generator.generate())
            .willReturn(jwtTokenWithExpDate(now().plus(1, MINUTES)));

        // but we want to refresh 2 minutes before it expires
        AutorefreshingJwtAuthTokenGenerator jwtGenerator = new AutorefreshingJwtAuthTokenGenerator(
            generator,
            Duration.of(2, MINUTES)
        );

        // when
        repeat(2, () -> jwtGenerator.generate());

        // then
        // it should request a new token
        verify(generator, times(2)).generate();
    }

    private String jwtTokenWithExpDate(Instant expAtDate) throws Exception {
        return JWT
            .create()
            .withExpiresAt(Date.from(expAtDate))
            .sign(HMAC256("secret"));
    }

    private void repeat(int times, Runnable action) {
        range(0, times).forEach(i -> action.run());
    }
}
