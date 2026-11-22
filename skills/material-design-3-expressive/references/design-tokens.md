# Material Design 3 Expressive - Design Tokens & Patterns

Comprehensive reference for Material Design 3 Expressive design tokens, styling rules, animations, and component patterns.

---

## Color System

Material Design 3 Expressive emphasizes high-expression, tonal palettes and dynamic color adaptations with clear semantic roles.

### Semantic Color Roles
- **Primary / On-Primary:** Key brand tone for emphasized actions, prominent buttons, and active states.
- **Primary Container / On-Primary Container:** Softer tint for highlighted cards, badges, and selected chips.
- **Secondary / On-Secondary:** Supporting tone for less prominent actions, secondary chips, and filters.
- **Secondary Container / On-Secondary Container:** Subtle container background for grouped components.
- **Tertiary / On-Tertiary:** Accent tone used for playful balance, contrasting badges, and expressive highlights.
- **Surface / On-Surface:** Core background and base cards; ensures text legibility with strict contrast.
- **Surface Variant / On-Surface Variant:** Subdued surfaces, borders, dividers, and placeholder text.
- **Outline / Outline Variant:** Subtle borders for glassmorphism / bento grid structures.
- **Error / On-Error / Error Container:** Informative feedback for validation and destructive actions.

### Dynamic Color & Expressive Palettes
- Utilize perceptual color spaces (e.g. `oklch` or dynamic M3 tonal palettes).
- Maintain WCAG 2.1 AA minimum contrast (4.5:1 for body text, 3:1 for large headlines and active UI controls).
- Enhance dark mode with desaturated surface tones and subtle luminance steps rather than harsh pure black.

---

## Component Patterns

Expressive component design blends modern tactile surfaces, friendly curvature, and structured layouts.

### Geometry & Shapes
- **Curvature Hierarchy:**
  - Extra Large (Containers, Bottom Sheets, Bento Cards): `28.dp` – `32.dp`
  - Large (Dialogs, Cards): `24.dp` – `28.dp`
  - Medium (Buttons, Menus, Chips): `16.dp` – `20.dp`
  - Small / Pill (Badges, Floating Indicators): `8.dp` – `12.dp` or Full Pill (`50%` / `CircleShape`)
- **Spatial / Bento Grid Layouts:**
  - Group related information into asymmetrical yet harmonious cards.
  - Border stroke: subtle translucent tint (`1.dp` solid with `12%` - `18%` opacity).
  - Surface elevations: combine subtle tonal tinting with soft ambient blur/shadow rather than harsh drop-shadows.

### Padding & Touch Targets
- Minimum interactive component size: `48.dp x 48.dp`.
- Internal container padding: generous `16.dp` to `24.dp` spacing to maintain breathing room.
- Grid item spacing: consistent `12.dp` or `16.dp` gutters.

---

## Animation & Motion

Expressive motion brings interfaces alive through natural, physics-inspired spring dynamics.

### Spring Dynamics & Easing
- **Spring Specs:**
  - Responsive / Snappy (Taps, Toggle, Icon state changes): `DampingRatioMediumBouncy` or `DampingRatioLowBouncy`, `StiffnessMedium` (~250-350ms feel).
  - Spatial / Sheet Transitions (Modal opening, Card expansion): `DampingRatioNoBouncy`, `StiffnessLow` (~300-450ms feel).
- **Duration Guidelines:**
  - Micro-interactions (icon morph, hover, press): 150ms – 250ms.
  - Standard component transitions (dialogs, accordion expansion): 250ms – 350ms.
  - Complex screen choreography (staggered list item entry): 300ms – 400ms max.
- **Performance:** Animate transforms (scale, translation, alpha) rather than layout re-measurement (height/width).

---

## Typography

Expressive typography pairs energetic display headers with legible, workhorse body text.

- **Hierarchy:**
  - Display / Headline: expressive, confident weights (SemiBold to ExtraBold) with proportional line heights (1.15 - 1.25).
  - Title / Subtitle: Medium to SemiBold for quick visual scannability.
  - Body: Regular to Medium, line height 1.4 - 1.6, line length 45-75 characters.
  - Label / Caption: Medium to SemiBold, clean tracking for compact badges and meta tags.
- **Tone:** Professional yet approachable, avoiding robotic or bloated verbiage.

---

## Accessibility

- **Contrast:** AA compliance across all light and dark mode variants.
- **Focus Rings:** Distinct, high-visibility focus borders with minimum `2.dp` stroke and visual offset.
- **Reduced Motion:** Respect system preferences (`prefers-reduced-motion`) by collapsing spring bounces into smooth short cross-fades.
- **Labels & Semantics:** Clear accessibility descriptions for all icon buttons, state changes, and interactive widgets.
