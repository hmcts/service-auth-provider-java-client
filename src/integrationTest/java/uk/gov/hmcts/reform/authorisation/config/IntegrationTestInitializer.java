package uk.gov.hmcts.reform.authorisation.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import uk.gov.hmcts.reform.authorisation.ServiceAuthAutoConfiguration;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(ServiceAuthAutoConfiguration.class)
public class IntegrationTestInitializer {
}