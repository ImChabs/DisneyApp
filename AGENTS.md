# DisneyApp Agent Guide

Use `docs/disneyapp_blueprint.md` as the product source of truth. Use `docs/disneyapp_design_system.md` as the visual design source of truth. Keep the app focused: a native Android portfolio app for browsing Disney characters, searching by name, viewing details, saving local favorites, and growing into focused Disney catalog sections such as Films, Shows, and Parks.

## Project Direction

- Build with Kotlin, Jetpack Compose, Material 3, Navigation 3, Ktor, Koin, Room, Coil, Coroutines, Flow, StateFlow, and SharedFlow.
- Follow Clean Architecture, MVI, unidirectional data flow, and feature-based package organization inside the single `:app` module.
- Handle Disney API responses safely. Missing, empty, or null fields must not crash the app; missing images need a placeholder or fallback.
- Lightweight local caching for previously loaded characters is allowed as a resilience feature; do not expand it into complex offline sync unless the blueprint changes.
- Keep the premium Disney-inspired visual language consistent with `docs/disneyapp_design_system.md`.
- Keep scope aligned with the blueprint. Do not add auth, accounts, custom backend, payments, social features, ads, or complex offline sync unless the blueprint changes.

## Local Skills

Before making related changes, consult the relevant skill in `.agents/skills`:

- `android-module-structure`: project structure, Clean Architecture boundaries, package/module boundaries, Gradle layout, and version catalog decisions. Use it for architectural boundaries and future modularization decisions; the current default is still a single `:app` module.
- `android-presentation-mvi`: ViewModels, state/actions/events, UI models, Root/Screen composable split, `StateFlow`, and one-time effects.
- `android-compose-ui`: Compose UI, previews, accessibility, design system work, lazy layouts, side effects, and recomposition/performance concerns.
- `android-data-layer`: Ktor, Room, DTOs, entities, mappers, data sources, repositories, and safe API handling.
- `android-error-handling`: typed `Result`, `DataError`, expected failure handling, and mapping errors to user-facing UI text.
- `android-navigation`: type-safe Compose Navigation, routes, feature nav graphs, and app-level graph wiring.
- `android-di-koin`: Koin modules, dependency registration, ViewModel injection, and `koinViewModel()`.
- `android-testing`: JUnit5, Turbine, fakes, coroutine tests, ViewModel tests, repository/use case tests, and Compose tests.

Reference skills by name or purpose in plans and explanations. Do not copy their full content into project docs or code comments.

## Working Rules

- Prefer small, reviewable changes that move the blueprint forward.
- Do not introduce abstractions, utilities, base classes, or generic frameworks unless they remove real duplication already present in the project.
- Keep the project single-module unless the developer explicitly asks for Gradle modularization.
- Put business rules in domain/use cases, data access in data sources or repositories, and rendering-only logic in composables.
- Keep domain models separate from DTOs and Room entities; use explicit mappers.
- Use `StateFlow` for screen state. For one-time UI effects, follow the existing feature pattern with `SharedFlow` or `Channel.receiveAsFlow()`. Prefer modeling critical events as state when the UI must not miss them.
- Build future catalog sections such as Films, Shows, and Parks with the same small-scope MVI and package-based structure used by characters.
- Before changing UI colors, banners, cards, placeholders, backgrounds, or visual state panels, consult `docs/disneyapp_design_system.md` and `android-compose-ui`.
- Do not add reusable color or gradient literals directly in feature screens; use the theme tokens and brushes documented in the design system.
- Prefer fakes over mocks for simple tests, and add tests around important business logic, ViewModels, repositories, and Flow behavior.
