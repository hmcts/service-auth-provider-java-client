package uk.gov.hmcts.reform.authorisation.healthcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.actuate.health.Status;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InternalHealth(Status status) {
}
