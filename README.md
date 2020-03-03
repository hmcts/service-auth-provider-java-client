[![Known Vulnerabilities](https://snyk.io/test/github/hmcts/service-auth-provider-java-client/badge.svg)](https://snyk.io/test/github/hmcts/service-auth-provider-java-client)
[ ![Download](https://api.bintray.com/packages/hmcts/hmcts-maven/service-auth-provider-client/images/download.svg) ](https://bintray.com/hmcts/hmcts-maven/service-auth-provider-client/_latestVersion)

# service-auth-provider-java-client

This is the client library for the service-auth-provider api microservice.
The tool provides a method to generate s2s auth token for a microservice and, optionally, caches it.


## Getting started

### Prerequisites

- [JDK 8](https://www.oracle.com/java)
- [Docker](https://www.docker.com)

### Building

The project uses [Gradle](https://gradle.org) as a build tool but you don't have install it locally since there is a
`./gradlew` wrapper script.  

To build project execute the following command:

```bash
    ./gradlew build
```
## Configuration
The following values must be provided:
```yaml
idam:
  s2s-auth:
    url: http://localhost:4502
    totp_secret: AAAAAAAAAAAAAAAC
    microservice: ccd_gw
```

A spring bean:
```java
   @Configuration
   public class ServiceTokenGeneratorConfiguration {
   
       @Bean
       public AuthTokenGenerator serviceAuthTokenGenerator(
               @Value("${idam.s2s-auth.totp_secret}") final String secret,
               @Value("${idam.s2s-auth.microservice}") final String microService,
               final ServiceAuthorisationApi serviceAuthorisationApi
       ) {
           return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
       }

   }
``` 
## Configuration for Service Authentication filter
The following values must be provided to enable a ServiceAuthFilter bean:
```yaml
idam:
  s2s-authorised:
    services: microservice1, microservice2
```
ServiceAuthFilter bean is OncePerRequestFilter filter that you can add to your filter chain to authorise service 
request. The filter will expect 'ServiceAuthorization' Bearer token as part of the request header that it will consume 
to approve the request. Any requests from services that are not in your authorised services list will deny access 
to your service and return an HTTP response status code 401.

## Developing

### Unit tests

To run all unit tests execute the following command:

```bash
    ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) execute the following command:

```bash
    ./gradlew check
```

## Versioning

We use [SemVer](http://semver.org/) for versioning.
For the versions available, see the tags on this repository.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
