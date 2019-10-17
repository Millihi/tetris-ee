# tetris-ee

Tetris, enterprise edition.

Java EE 7 techs — WebSocket JSR-356, JSF 2.2, EJB 3.1, JPA 2.0.

## Build requirements

1. captcha.auth — the wsimport authfile with credentials for the captcha
service.

2. application.${config.type}.properties — application properties for selected
build profile.

## Run requirements

- Java EE 7 compilant application server.
- Database with already created tables as specified in the META-INF/sql/create.sql
- Access to the captcha web-service.
