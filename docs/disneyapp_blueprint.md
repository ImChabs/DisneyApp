# DisneyApp Blueprint

## Project Description

DisneyApp is a simple native Android portfolio app for exploring Disney characters using the public Disney API.

The app should let users browse characters, search by name, open character details, and save favorite characters locally.

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
- Basic unit and Flow testing.
- Manual development with Codex assistance.

Codex can help with planning, implementation, refactoring, debugging, and tests, but every change should be reviewed and validated manually by the developer.

## App Requirements

The app should include:

- A character list screen.
- A character detail screen.
- Search characters by name.
- Save and remove favorite characters locally.
- A favorites screen.
- Loading states.
- Error states.
- Empty states.
- Basic tests for important logic.

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
- SharedFlow for one-time effects when needed

## Architecture

The app should follow:

- Clean Architecture
- MVI
- Unidirectional Data Flow
- Feature-based package organization

Architecture details and implementation rules should be defined in `AGENTS.md` and/or project skills, not duplicated heavily in this blueprint.

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
- Monetization or ads.
- Overly complex UI/UX features.

The app should stay small enough to be completed and polished as a portfolio project.
