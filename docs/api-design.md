# API Design (Draft)
- 인증: JWT (Access/Refresh), Firebase ID Token, Google ID Token
- RBAC: ROLE_USER / ROLE_ADMIN
- 전역 Rate Limit: 100 req / 60s (Redis)
- 페이지네이션: `page`, `size`, `sort`
- 에러 포맷: `ApiErrorResponse` (timestamp, path, status, code, message, details)
