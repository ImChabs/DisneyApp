# DisneyApp Blueprint

## Project Description

DisneyApp is a simple native Android portfolio app for exploring Disney catalog content using the public Disney API.

The current app lets users browse characters, search by name, open character details, and save favorite characters locally.

Future sections should expand the catalog in focused increments, such as Films, Shows, and Parks / attractions, while staying within the same portfolio-app scope.

The app should keep a consistent premium Disney-inspired visual style. Visual direction, color tokens, gradients, and reusable UI styling rules are defined in `docs/disneyapp_design_system.md`.

This project is primarily a learning and portfolio app. The scope should stay focused, clean, and practical.

## Goal

The goal of this project is to practice building a modern Android app with:

- Remote API consumption.
- Clean Architecture.
- MVI.
- Unidirectional Data Flow.
- Jetpack Compose UI.
- Local persistence for favorites.
- Lightweight local caching for previously loaded characters.
- Basic unit and Flow testing.
- Manual development with Codex assistance.

Codex can help with planning, implementation, refactoring, debugging, and tests, but every change should be reviewed and validated manually by the developer.

## Current App

The current app includes:

- A character list screen.
- A character detail screen.
- Search characters by name.
- Save and remove favorite characters locally.
- A favorites screen.
- Loading states.
- Error states.
- Empty states.
- Lightweight cached fallback for previously loaded character list/detail data when remote loading fails.
- Basic tests for important logic.

## Planned Additions

Future work should add focused Disney catalog sections without turning the app into a broad media platform.

Planned areas include:

- Films.
- Shows.
- Parks and attractions.
- Character-driven discovery surfaces that reuse existing Disney API data where practical.

Each new section should follow the same architecture and visual direction as the character experience.

## API

The app will use the public Disney API.

Base URL:

```txt
https://api.disneyapi.dev
```

Main endpoints:

```txt
GET /character
GET /character/{id}
GET /character?name={name}
```

The app should handle API responses safely and should not crash when fields are missing, empty, or null.

If a character image is missing, the UI should show a placeholder or a safe fallback.

## Tech Stack

The project should use:

- Native Android
- Kotlin
- Jetpack Compose
- Material 3 Design
- Navigation 3
- Ktor Client
- Koin
- Room
- Coil
- Coroutines
- Flow
- StateFlow for screen state
- SharedFlow or Channel-backed Flow for one-time effects when needed

## Architecture

The app should follow:

- Clean Architecture
- MVI
- Unidirectional Data Flow
- Feature-based package organization inside the single `:app` module

The project is intentionally single-module for now. Internal package boundaries should still separate `core`, feature `domain`, feature `data`, and feature `presentation` concerns.

Architecture details and implementation rules should be defined in `AGENTS.md` and/or project skills, not duplicated heavily in this blueprint.

## Local Data

The app persists favorite characters locally with Room.

The app may also keep a lightweight local cache of previously loaded character list/detail data so users can see saved results when remote loading fails.

This cache is a resilience feature, not a complex offline-first synchronization system. Avoid background sync, conflict resolution, account-scoped data, or large offline workflows unless this blueprint changes.

Search can remain remote-only unless a future requirement explicitly changes that behavior.

## Testing

The project should use:

- JUnit 5 for unit testing.
- MockK for mocking dependencies when interaction verification is needed.
- Fakes for simple and controlled test dependencies.
- Coroutines Test for coroutine and asynchronous testing.
- Turbine for Flow, StateFlow, and SharedFlow testing.

Testing should focus first on important business logic, ViewModels, use cases, repositories, and Flow behavior.

## Out of Scope

For now, the app should not include:

- Login or authentication.
- User accounts.
- A custom backend.
- Payments.
- Social features.
- Admin features.
- Character creation or editing.
- Complex offline-first synchronization.
- Broad media-platform features beyond focused catalog browsing.
- Monetization or ads.
- Overly complex UI/UX features.

The app should stay small enough to be completed and polished as a portfolio project.
