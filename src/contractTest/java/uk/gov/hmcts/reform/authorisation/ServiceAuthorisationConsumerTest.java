package uk.gov.hmcts.reform.authorisation;

import au.com.dius.pact.consumer.dsl.PactDslRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoConfiguration
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "s2s_auth", port = "5050")
@SpringBootTest(
    classes = ServiceAuthorisationApi.class,
    properties = {
        "idam.s2s-auth.url=http://localhost:5050"
    }
)
class ServiceAuthorisationConsumerTest {

    private static final String AUTHORISATION_TOKEN = "Bearer someAuthorisationToken";
    private static final String SOME_MICRO_SERVICE_NAME = "someMicroServiceName";
    private static final String SOME_MICRO_SERVICE_TOKEN = "someMicroServiceToken";

    @Autowired
    private ServiceAuthorisationApi serviceAuthorisationApi;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, String> jsonPayload = new HashMap<>();

    @BeforeEach
    void setUpTest() {
        jsonPayload.clear();
        jsonPayload.put("microservice", "microserviceName");
        jsonPayload.put("oneTimePassword", "784467");
    }

    @Pact(consumer = "s2s_auth_client")
    V4Pact executeLease(PactDslWithProvider builder) throws JsonProcessingException {
        return builder.given("microservice with valid credentials")
            .uponReceiving("a request for a token")
            .path("/lease")
            .method(HttpMethod.POST.name())
            .body(buildJsonPayload())
            .willRespondWith()
            .headers(Map.of(HttpHeaders.CONTENT_TYPE, "text/plain"))
            .status(HttpStatus.OK.value())
            .body(PactDslRootValue.stringType(SOME_MICRO_SERVICE_TOKEN))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "s2s_auth_client")
    V4Pact executeDetails(PactDslWithProvider builder) {
        return builder.given("microservice with valid token")
            .uponReceiving("a request to validate details")
            .path("/details")
            .headers(HttpHeaders.AUTHORIZATION, AUTHORISATION_TOKEN)
            .method(HttpMethod.GET.name())
            .willRespondWith()
            .headers(Map.of(HttpHeaders.CONTENT_TYPE, "text/plain"))
            .status(HttpStatus.OK.value())
            .body(PactDslRootValue.stringType(SOME_MICRO_SERVICE_NAME))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "executeLease")
    void verifyLease() {
        String token = serviceAuthorisationApi.serviceToken(jsonPayload);

        assertEquals(SOME_MICRO_SERVICE_TOKEN, token);
    }

    @Test
    @PactTestFor(pactMethod = "executeDetails")
    void verifyDetails() {
        String serviceName = serviceAuthorisationApi.getServiceName(AUTHORISATION_TOKEN);

        assertEquals(SOME_MICRO_SERVICE_NAME, serviceName);
    }

    private String buildJsonPayload() throws JsonProcessingException {
        return objectMapper.writeValueAsString(jsonPayload);
    }
}
