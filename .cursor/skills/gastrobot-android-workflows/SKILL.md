---
name: gastrobot-android-workflows
description: >-
  Implements GastroBot Android features using MVVM, Compose Route/Screen split,
  ViewModel UiState and events, string resources for all locales, and data-layer
  mapping. Use when adding or refactoring screens, navigation, API-backed
  features, copy/strings, or when the user asks for project-specific Android
  workflow guidance.
---

# GastroBot Android workflows

## New screen or flow (checklist)

1. **Strings** — Add keys in `app/src/main/res/values/strings.xml` and every other locale folder (e.g. `values-sr`); use `…` or `&#8230;`, not `...`.
2. **Data** — If new backend data is needed: define or extend DTOs under `network`, map to domain or app models in the repository; expose only non-DTO types toward UI.
3. **Repository** — Add or extend repository interfaces and implementations; surface explicit error handling consistent with the module.
4. **ViewModel** — Single `UiState`, `StateFlow` for state, sealed events for one-off actions; `viewModelScope`, no `Context`/Compose imports.
5. **UI** — `*Route` + `*Screen`; stateless composables where possible; `collectAsStateWithLifecycle`; loading and error feedback with retry when appropriate.
6. **Navigation** — Register the destination in the appropriate `NavHost` / route graph; keep navigation out of generic reusable components.
7. **Previews** — `@Preview` on the main screen composable and important reusable UI.

## Changing copy only

- Update `strings.xml` (and matching keys in all `values-*` folders).
- Replace literals in Kotlin/XML with `stringResource` / `@string/`.

## New or changed API endpoint

- Service interface + DTOs in `network` (Retrofit/OkHttp).
- Map DTOs in the repository layer; handle errors with the project’s result pattern.
- ViewModel calls repository only, never Retrofit directly from UI.

## Quick reference

- Rules: `.cursor/rules/kotlin-android-core.mdc` (always), plus scoped rules for `ui`, `*ViewModel.kt`, `network`/`repository`, and `res/values*`.
