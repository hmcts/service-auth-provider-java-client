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

To use the services provided by this clients, they need to be instantiated in spring `@Configuration` class, for example:

```java
   @Configuration
   @Lazy
   @EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
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
