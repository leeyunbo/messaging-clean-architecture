---
name: reply
description: 코드 리뷰 코멘트에 AI 답변 달기
disable-model-invocation: true
---

`.review/comments.json` 파일을 읽고 리뷰 코멘트에 답변해줘.

각 코멘트에 대해:
1. `replies` 배열이 없거나, 마지막 reply의 author가 "user"인 코멘트만 처리해
2. `file` 경로로 실제 소스 파일을 읽어
3. `line` 번호 주변 코드 컨텍스트를 파악해
4. 원본 `content`와 이전 `replies` 대화 내용을 모두 참고해
5. 새 답변을 `replies` 배열에 추가해:
   ```json
   { "author": "claude", "content": "답변 내용", "createdAt": "ISO날짜" }
   ```
6. comments.json 파일을 직접 수정해 (Edit 도구 사용)

기존 aiReply 필드가 있으면 replies 배열로 마이그레이션해:
```json
"replies": [{ "author": "claude", "content": "기존 aiReply 내용" }]
```

답변 저장 후 브라우저를 새로고침하면 대화 스레드가 보임.
