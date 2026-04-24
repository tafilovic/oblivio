# Codex Project Rules

1. Do not run project build or compile commands after making changes unless the user explicitly asks for it.
2. Never hardcode UI strings. Always use string resources (`strings.xml`) and add/update translations in all existing `values-*` locale files.
3. Always create `@Preview` composables for every new UI component/screen (UI only, not non-UI classes).
4. Do not resolve string resources inside ViewModels. ViewModels should expose resource IDs (or structured UI state), and composables/screens should resolve them via `stringResource(...)`.
