# DisneyCast Design System

This document is the source of truth for DisneyCast's visual direction. Keep the app visually consistent across character list, detail, favorites, loading, empty, and error states.

## Visual Direction

DisneyCast uses a premium Disney-inspired style:

- dark cinematic backgrounds with deep navy and plum tones
- gold as the main magical accent
- lavender and violet as secondary accents
- soft gradient banners, panels, and placeholders
- translucent surfaces over dark backgrounds
- readable light text on premium/dark surfaces

The UI should feel polished and portfolio-ready while staying practical and focused on browsing Disney characters.

## Color And Brush Rules

- Do not add `Color(0x...)` literals inside feature screens or feature components.
- Put reusable colors in `DisneyColors`.
- Put reusable gradients in `DisneyBrushes`.
- Use `DisneyCastTheme` for Material 3 theming.
- Keep `dynamicColor = false` by default so the brand look stays consistent across devices.
- `Color.White`, `Color.Black`, and `Color.Transparent` are acceptable in composables for simple local overlays, scrims, and transparent containers.
- If a color or gradient is used more than once, or represents the app's identity, promote it to `ui/theme`.

## Current Tokens

The current visual API lives in:

- `app/src/main/java/com/example/disneycast/ui/theme/Color.kt`
- `app/src/main/java/com/example/disneycast/ui/theme/Brushes.kt`
- `app/src/main/java/com/example/disneycast/ui/theme/Theme.kt`

Use `DisneyColors` for semantic color choices such as background, ink surfaces, gold accents, lavender accents, and text on dark backgrounds.

Use `DisneyBrushes` for shared visual treatments such as catalog backgrounds, favorites backgrounds, hero banners, premium panels, detail headers, loading placeholders, and image fallbacks.

## Components And Patterns

- Screens should reuse the shared premium backgrounds instead of redefining gradients locally.
- Banners and hero sections should use shared brushes and remain content-focused.
- Cards and action surfaces should use translucent dark containers, light borders, and gold accents where appropriate.
- Loading, empty, and error states should use the same premium visual language as normal content.
- Missing character images should use the shared image placeholder treatment, not a plain flat color.

## Maintenance Rules

- When changing the app's visual identity, update this document and the tokens together.
- Prefer small, reviewable token additions over one-off styling inside screens.
- Do not duplicate this document in `AGENTS.md` or `docs/disneycast_blueprint.md`; those files should only reference it.
- Keep visual rules aligned with `docs/disneycast_blueprint.md`: no marketing pages, auth, social features, payments, ads, or unrelated product expansion.
