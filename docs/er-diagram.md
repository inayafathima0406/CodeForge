# CodeForge Database Design (ER Diagram)

```mermaid
erDiagram
    ROLES ||--o{ USERS : has
    USERS ||--|| USER_STATS : has
    TOPICS ||--o{ QUESTIONS : groups
    QUESTIONS ||--o{ TEST_CASES : has
    USERS ||--o{ SUBMISSIONS : makes
    QUESTIONS ||--o{ SUBMISSIONS : receives
    LANGUAGES ||--o{ SUBMISSIONS : uses
    SUBMISSIONS ||--o{ SUBMISSION_RESULTS : has
    TEST_CASES ||--o{ SUBMISSION_RESULTS : checked_by
    SUBMISSIONS ||--o| AI_FEEDBACK : gets
    USERS ||--o{ USER_QUESTION_PROGRESS : tracks
    QUESTIONS ||--o{ USER_QUESTION_PROGRESS : tracked_in
    CODING_TESTS ||--o{ CODING_TEST_QUESTIONS : contains
    QUESTIONS ||--o{ CODING_TEST_QUESTIONS : used_in
    USERS ||--o{ TEST_ATTEMPTS : takes
    CODING_TESTS ||--o{ TEST_ATTEMPTS : attempted_as
    TEST_ATTEMPTS ||--o{ SUBMISSIONS : includes
    USERS ||--o{ USER_BADGES : earns
    BADGES ||--o{ USER_BADGES : awarded_as
```

## How to read the notation

- `||--o{` means "one to many". For example, one topic has many questions.
- `||--||` means "one to one". Each user has exactly one `user_stats` row.
- `||--o|` means "one to zero-or-one". A submission may or may not have AI feedback.