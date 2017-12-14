package uk.gov.hmcts.reform.authorisation.healthcheck;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.actuate.health.Status;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalHealth {
    private final Status status;

    @JsonCreator
    public InternalHealth(
        @JsonProperty("status") Status status
    ) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
