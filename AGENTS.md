## Project overview
- Native Android app written in Kotlin.
- Architecture: Clean Architecture (`data -> domain -> ui`).
- Dependency injection: Koin.
- Persistence: Room.
- Pagination: Paging3 with RemoteMediator.
- UI: Jetpack Compose.
- Navigation: Navigation Compose with typed `@Serializable` destinations.
- Book ID type: `Int`.

## Core architectural rules
- Room is the single source of truth.
- UI must read state from Room-backed `Flow` / `PagingData`, not directly from network responses.
- ViewModels must use use cases, not DAOs directly.
- Use cases must depend on repository interfaces.
- Do not introduce string-based routes; keep typed destinations.

## RemoteMediator rules
- RemoteMediator writes network results into Room.
- Local-only fields must not be overwritten by sync.
- When updating existing rows, preserve local-only columns such as:
    - `isFavorite`
    - reading progress / local flags if they exist

## Change constraints
- Prefer minimal, localized changes.
- Do not add new libraries unless explicitly requested.
- Do not refactor unrelated code.
- Follow existing naming conventions:
    - `*Entity`
    - `*Dao`
    - `*Repository` / `*Repo`
    - `*UC`
    - `*ViewModel`

## Implementation expectations
- For non-trivial work, plan first.
- First output should be:
    1. plan
    2. exact file list to change/create
- Then implement only those files.
- Keep solutions simple and aligned with existing patterns.

## Verification
Before considering work done:
- build the affected module(s)
- run relevant unit tests if present
- ensure paging behavior still works
- ensure Room remains source of truth
- ensure navigation still works

## Feature guidance
For filtering and similar features:
- Prefer DB-first implementations before API-first ones, unless explicitly requested otherwise.
- Recreate paging streams reactively from filter state.
- Keep filter state explicit and easy to reason about.

## If unsure
- Choose the simplest solution consistent with current architecture.
- Ask for clarification only if a missing detail blocks a correct implementation.