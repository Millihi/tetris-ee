# tetris-ee

Tetris, enterprise edition.

Java EE 7 techs — WebSocket JSR-356, JSF 2.2, EJB 3.1, JPA 2.0.

Tested on the GlassFish 4, WildFly 17 and TomEE 8.

## Build requirements

- captcha.auth — the wsimport authfile with credentials for the captcha
  service. See wsimport help for details on the format of this file.
- application.${config.type}.properties — application properties for selected
  build profile. Defined profiles are "Development" and "Production".
  See pom.xml for details.
- An access to the captcha web-service.

## Run requirements

- Java EE 7 compilant application server.
- Database with already created tables as specified in the
  META-INF/sql/create.sql
- Access to the captcha web-service.

### Notes
- Has creepy design :-)
- Designed for maximum portability (in simple meaning — does not use of the
  vendor-specific features). However the persistence.xml lacks portability
  due to JPA providers imperfections.
