# Budget Microservice

[![Logo](https://img.shields.io/badge/vert.x-3.9.3-purple.svg)](https://vertx.io")

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
* Vert.x - Version 3.9.3
* Log4j2 - Version 2.13.3

---

## Environment Variables
Following table is a **mandatory** environment variables used in this project.

| Variable Name | Datatype | Description |
| --- | --- | --- |
| APP_PORT | Number | Server port |

Following table is a **optional** environment variables used in this project.
| Variable Name | Datatype | Description | Default Value |
| --- | --- | --- | --- |
| APP_HOST | String | Server host | localhost |
| LOG_LEVEL | String | Log level | error |

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
