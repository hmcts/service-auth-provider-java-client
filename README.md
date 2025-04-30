# service-auth-provider-java-client

This is the client library for the service-auth-provider api microservice.
The tool provides a method to generate s2s auth token for a microservice and, optionally, caches it.


## Getting started

This library is hosted on Azure DevOps Artifacts and can be used in your project by adding the following to your `build.gradle` file:

```gradle
repositories {
    maven {
        url 'https://pkgs.dev.azure.com/hmcts/Artifacts/_packaging/hmcts-lib/maven/v1'
    }
}

dependencies {
  implementation 'com.github.hmcts:service-auth-provider-java-client:LATEST_TAG'
}
```


### Prerequisites

- [Java 17](https://adoptium.net/)
- [Docker](https://www.docker.com)

### Building

The project uses [Gradle](https://gradle.org) as a build tool, but you don't have to install it locally since there is a
`./gradlew` wrapper script.  

To build the project run the following command:

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
ServiceAuthFilter bean is a `OncePerRequestFilter` filter that you can add to your filter chain to authorise a service 
request. The filter will expect a header with '`ServiceAuthorization: Bearer <token>`' as part of the request header that it will consume 
to approve the request. Any requests from services that are not in your authorised services list will deny access 
to your service and return an HTTP response status code 403 (forbidden) and for any other reasons if the token is
missing, invalid or failure to verify will result in 401(unauthorized).

## Running without Spring

You might want to use this client when not running in a spring context, i.e. a scheduled job possibly.

```java
class ServiceTokenGenerator {
    private static AuthTokenGenerator getAuthTokenGenerator(String s2sURL, String clientId, String clientSecret) {
        HttpMessageConverter<?> jsonConverter = new MappingJackson2HttpMessageConverter(new ObjectMapper());
        ObjectFactory<HttpMessageConverters> converter = () -> new HttpMessageConverters(jsonConverter);
    
        ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new SpringEncoder(converter))
                .decoder(new StringDecoder())
                .target(ServiceAuthorisationApi.class, s2sURL);
    
        return AuthTokenGeneratorFactory
                .createDefaultGenerator(clientSecret, clientId, serviceAuthorisationApi);
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

To release a new version add a tag with the version number and push this up to the origin repository. This will then 
build and publish the release to maven.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
