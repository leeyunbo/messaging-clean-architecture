# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A unified messaging gateway platform using **Clean Architecture + Monorepo**. Consolidates multiple message channels (SMS, LMS/MMS, RCS, Kakao Alimtalk/BrandMessage, Naver) into a single codebase while maintaining channel-level independence and independent deployments.

**Core Philosophy**: "Integrate but maintain independence" - single codebase for code sharing, but each channel remains independently deployable.

## Build Commands

```bash
# Build
./gradlew build                              # Build all modules and run tests
./gradlew :message-bootstrap:sms-sender:build  # Build specific module

# Testing
./gradlew test                               # Run all unit tests
./gradlew :message-core:sms-domain:test      # Run single module tests
./gradlew pitest                             # Mutation testing
./gradlew jacocoTestReport                   # Code coverage (70% minimum required)

# Running Applications
./gradlew :message-bootstrap:sms-sender:bootRun
./gradlew :message-bootstrap:kakao-alimtalk-sender:bootRun
```

## Architecture

### Layered Structure (43 modules)

```
message-bootstrap/     (14) - Executable Spring Boot applications
message-usecase/       (6)  - Business logic, orchestration
message-core/          (6)  - Pure domain models, port interfaces
message-platform/      (7)  - External API adapters (SKT, KT, Kakao, etc.)
message-infrastructure/ (6) - Database (R2DBC), RabbitMQ, Netty
message-library/       (3)  - Shared utilities (id-generator, logging, webhook)
build-logic/               - Gradle convention plugins
```

### Dependency Flow

```
Bootstrap ──┐
Platform ───┼──> UseCase ──> Core (pure domain, no external deps)
Infrastructure ──┘
Library ─────────────────────────────────>
```

### Bounded Contexts

Each message channel is a separate bounded context with its own domain model:
- SMS/LMS/MMS (sms-domain, lms-mms-usecase)
- Kakao (kakao-domain) - Alimtalk and BrandMessage
- RCS (rcs-domain)
- Naver (naver-domain)
- Shared: partner-domain, report-domain

### Convention Plugins

Located in `build-logic/src/main/kotlin/`:
- `kotlin-conventions` - Base: Kotlin 2.x, JVM 21, JaCoCo (70% coverage)
- `core-conventions` - For domain modules
- `usecase-conventions` - Adds Spring Context, Resilience4j, Pitest
- `infrastructure-conventions` - Spring Boot Starter, java-library
- `platform-conventions` - WebFlux, CircuitBreaker, Retry, Jackson 3.x
- `bootstrap-conventions` - Spring Boot application, WebFlux, Actuator
- `library-conventions` - Minimal shared utilities

## Tech Stack

- **Kotlin 2.3** / JVM 21
- **Spring Boot 4.0.1** with WebFlux (reactive)
- **Gradle 9.2.1** with Kotlin DSL
- **R2DBC** (reactive database) / MySQL
- **RabbitMQ** with Spring AMQP
- **Resilience4j** (CircuitBreaker, Retry)
- **Netty** for TCP/SMPP protocol
- **Jackson 3.x** (with 2.x compatibility)

## Module Dependency Example

SMS channel dependency chain:
```
sms-sender (bootstrap)
├── sms-domain (core) - ports
├── sms-usecase (usecase) - business logic
├── skt-platform, kt-platform, lgt-platform (platform)
├── db-sms (infrastructure)
├── rabbitmq (infrastructure)
└── logging, id-generator (library)
```

## Version Management

Centralized in `build-logic/src/main/kotlin/Versions.kt`.

---

# /reply

`.review/comments.json` 파일을 읽고 리뷰 코멘트에 답변해줘.

각 코멘트에 대해:
1. `aiReply`가 없는 코멘트만 처리해
2. `file` 경로로 실제 소스 파일을 읽어
3. `line` 번호 주변 코드 컨텍스트를 파악해
4. `content`에 있는 질문/코멘트에 대해 답변을 작성해
5. 해당 코멘트의 `aiReply` 필드에 답변을 저장해 (comments.json 직접 수정)

답변 저장 후 브라우저를 새로고침하면 대댓글이 보임.
