# Budget Microservice

[![Logo](https://img.shields.io/badge/vert.x-3.9.4-purple.svg)](https://vertx.io")
![deploy](https://github.com/anas-didi95/vertx-budget-server/workflows/deploy/badge.svg)
![build](https://github.com/anas-didi95/vertx-budget-server/workflows/build/badge.svg)

This application was generated using http://start.vertx.io

---

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Environment variables](#environment-variables)
* [Setup](#setup)
* [References](#references)
* [Contact](#contact)

---

## General info
Back-end service which provides and handles budget-related resources such as expenses and incomes.

---

## Technologies
* Vert.x - Version 3.9.4
* Log4j2 - Version 2.14.0

---

## Environment Variables
Following table is a **mandatory** environment variables used in this project.

| Variable Name | Datatype | Description |
| --- | --- | --- |
| APP_HOST | String | Server host |
| APP_PORT | Number | Server port |
| LOG_LEVEL | String | Log level |
| MONGO_CONNECTION_STRING | String | Mongo connection string (refer [doc](https://docs.mongodb.com/manual/reference/connection-string/) for example) |
| GRAPHIQL_ENABLE | Boolean | Flag to enable GraphiQL |
| JWT_SECRET | String | JWT secret key for signature of token |
| JWT_ISSUER | String | JWT issuer for token validation |
| JWT_EXPIRE_IN_MINUTES | Number | JWT token expiration period (in minutes) |

---

## Setup
To launch your tests:
```
./mvnw clean test
```

To package your application:
```
./mvnw clean package
```

To run your application:
```
./mvnw clean compile exec:java
```

---

## References
* [Vert.x Documentation](https://vertx.io/docs/)
* [Vert.x Stack Overflow](https://stackoverflow.com/questions/tagged/vert.x?sort=newest&pageSize=15)
* [Vert.x User Group](https://groups.google.com/forum/?fromgroups#!forum/vertx)
* [Vert.x Gitter](https://gitter.im/eclipse-vertx/vertx-users)

---

## Contact
Created by [Anas Juwaidi](mailto:anas.didi95@gmail.com)
