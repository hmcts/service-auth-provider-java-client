package uk.gov.hmcts.reform.authorisation.exceptions;

import uk.gov.hmcts.reform.logging.exception.AlertLevel;
import uk.gov.hmcts.reform.logging.exception.UnknownErrorCodeException;

/**
 * SonarQube reports as error. Max allowed - 5 parents
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ServiceException extends UnknownErrorCodeException {

    public ServiceException(String message, Throwable cause) {
        super(AlertLevel.P1, message, cause);
    }
}
