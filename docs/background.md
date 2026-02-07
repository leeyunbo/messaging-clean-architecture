# 프로젝트 배경

> 이 문서는 "당근페이 백엔드 아키텍처가 걸어온 여정" 포스트를 읽고 작성한 문서입니다.
> 가급적 [원문 포스트](https://medium.com/daangn/당근페이-백엔드-아키텍처가-걸어온-여정-3a6f69f1eb11)를 통해 학습하는 것을 권장드립니다.

---

## 1. 조직의 아키텍처

> Any organization that designs a system (defined broadly) will produce a design whose structure is a copy of the organization's communication structure.
>
> — Melvin Conway

**어떤 조직이 시스템을 설계하면, 그 조직의 커뮤니케이션 구조를 본뜬 형태가 된다.**

우리 팀은 어떤 조직일까? 사실 내가 생각하기엔 **단절된 커뮤니케이션 문화**를 가지고 있는 조직이라고 생각한다.

우리 팀은 수많은 발송 미디어의 발송을 책임지는 백엔드 시스템(이하 Gateway)을 운영하는 팀이다. 각 발송 미디어는 굉장히 유사한 비즈니스 로직을 가지고 있지만, 책임지는 발송 미디어가 다르다는 이유로 별도 프로젝트로 구분되어 있다.

이 상태로 요구사항이 많아지고, 담당하고 있는 Gateway의 업무 처리에 급급하다보니 팀원들 간의 커뮤니케이션이 거의 단절된 상태가 되어버렸다.

그렇다 보니 다른 팀원이 담당하는 Gateway에 대해서는 거의 알지 못하는 상황이고, 심지어 팀의 리더도 본인이 담당하던 Gateway 외에는 자세히 파악하지 못하는 경우가 많다. (동료들이 부족하다는게 아니라 아키텍처의 한계때문에 어쩔 수 없다!)

활발한 코드 리뷰를 도입하려고 해도 각 Gateway에 대한 이해도가 낮아서 불가능하고, 공유 문화를 만들려고 해도 이해도가 낮으니 활성화가 안 된다.

결국 우리 팀은 단절된 커뮤니케이션 문화를 가진 조직이고, **이런 조직을 만든 원인이자 결과물이 현재의 아키텍처**라고 생각한다.

우리 팀의 커뮤니케이션 문화를 개선하려면, 결국 분산되어 있는 Gateway들을 하나의 프로젝트에서 관리할 수 있는 아키텍처로 전환하는 것이 필요하다고 생각한다.

---

## 2. 아키텍처 패턴 검토

Hexagonal Architecture, Clean Architecture, Onion Architecture...
이것들은 본질적으로 같은 원칙을 공유한다.
- 도메인을 외부 기술로부터 분리
- 의존성은 바깥에서 안쪽으로만

하지만 이런 아키텍처 패턴들은 **컴포넌트 내부의 클래스 간 의존성**을 다루는 것이지, 내가 목표로 하는 **여러 Gateway를 하나로 합치는 문제**와는 거리가 멀다.

우리에게 필요한 건 아키텍처 "패턴"이 아니라 아키텍처 "구조"였다.

---

## 3. Monorepo + Bounded Context + 재사용성

결국 우리가 필요한 건 아래 세가지라고 생각한다.

1. **Monorepo**: 분산된 Gateway들을 하나의 코드베이스로
2. **Bounded Context**: 채널별 도메인 경계를 명확하게 분리
3. **재사용성**: 공통으로 사용되는 도메인 모듈은 재사용을 위해 DIP 원칙을 확실히 준수

당근페이의 Clean Architecture의 모듈 구조(bootstrap, core, usecase, infrastructure, platform, library)는 이런 요구사항을 만족시키는 좋은 레퍼런스가 되었다.

---

## 4. Clean Architecture & Monorepo

당근페이는 아키텍처를 크게 bootstrap, core, infrastructure, library, platform, usecase로 나눴다. 각 모듈은 독립적으로 개발, 테스트, 교체될 수 있다는 특징이 있다.

| 레이어 | 역할 |
|--------|------|
| **bootstrap** | 모든 레이어의 의존성을 조립해서 애플리케이션을 실행하는 최상위 모듈 |
| **core** | 각 도메인의 핵심 비즈니스 규칙을 정의하는 모듈 (외부 기술 의존 X) |
| **infrastructure** | 애플리케이션 실행에 필요한 기술적인 세부사항 |
| **library** | Logging, Retry 등 모든 모듈에서 공통으로 사용하는 횡단 관심사 모듈 |
| **platform** | 도메인과 관련된 외부 플랫폼을 연동한 모듈 |
| **usecase** | 사용자 시나리오 단위의 비즈니스 로직을 작성하는 모듈 |

---

## 5. Gateway에 적용한다면?

```
messaging-platform/
├── bootstrap/
│   ├── kakao-receiver
│   ├── kakao-sender
│   ├── naver-receiver
│   ├── naver-sender
│   └── ...
├── core/
│   ├── partner-domain
│   ├── kakao-domain
│   ├── sms-domain
│   └── ...
├── infrastructure/
│   ├── db
│   ├── rabbitmq
│   ├── webclient
│   └── netty
├── usecase/
│   ├── kakao-usecase
│   ├── rcs-usecase
│   ├── naver-usecase
│   └── sms-usecase
├── library/
│   ├── logging
│   └── retry
└── platform/
    ├── kakao
    ├── rcs
    └── ...
```

---

## 6. 기대 효과

### 기술적 효과

- **Bounded Context 분리**: 각 발송 미디어의 경계가 명확하게 분리됨
- **독립성 보장**: 모든 Gateway가 완전한 독립성을 가지며 의존성 침범 방지
- **변경 영향 최소화**:
  - SKT API 스펙 변경 → `platform:skt` + `bootstrap:sms-sender`만 수정/배포
  - 카카오 API 스펙 변경 → `platform:kakao` + `bootstrap:kakao-sender`만 영향
- **재사용성**: 공통 도메인, Infrastructure, Library가 모듈화되어 재사용 극대화
- **확장 용이**: 신규 미디어 타입 추가 시 기존 모듈 조합으로 손쉽게 구성

### 조직적 효과

- **코드 리뷰 활성화**: 모두가 함께 담당하는 프로젝트이기 때문에 리뷰 문화 부활
- **공유된 목표**: 나만의 불편함이 아닌 모두의 불편함 → 개선 목표가 모두의 것
- **협업 강화**: 문제가 발생해도 개인이 아니라 모두가 함께 해결
- **커뮤니케이션 개선**: 팀원 모두가 같은 목표를 바라보며 단절된 문화 해소

---

## 결론

> 팀원 모두가 같은 목표를 바라볼 수 있기 때문에, 단절된 커뮤니케이션 문화가 되살아날 수 있을 것이라 확신한다.
