# Motion language — Dompetku

## Design system
Material 3 Expressive motion system with organic spring-based physics, tonal color harmony, and calm data transitions.

## Fallback
Pragmatic M3 Expressive defaults: spatial springs for containers/controls, damping-ratio 1.0 (no bounce) for data elements.

## Specs

### Top-level destinations
- **Intent** — Unordered primary destinations cross-fade without lateral directional sliding.
- **Binding** — `MotionTokens.topLevelTransition`
- **Tag** — `OBSERVED`

### Floating dock navigation items
- **Intent** — Tonal selection indicator responds with organic spring, expanding label horizontally.
- **Binding** — `MotionTokens.colorTransition`
- **Tag** — `OBSERVED`

### Sheets and modal dialogs
- **Intent** — Enters with gentle spatial elevation and effects fade, exits cleanly on effects alone.
- **Binding** — `MotionTokens.modalEnter` and `MotionTokens.modalExit`
- **Tag** — `OBSERVED`

### Progress bars and charts
- **Intent** — Settles steadily without bounce so values look authoritative and accurate.
- **Binding** — `MotionTokens.dataProgressSpec`
- **Tag** — `OBSERVED`

### Interactive buttons and cards
- **Intent** — Expressive touch ripples with responsive spring deformation on state toggle.
- **Binding** — `MotionTokens.spatialSpringFloat`
- **Tag** — `OBSERVED`

## Frequency map
- **Never animate** — High-frequency tabular data cell redraws, transaction item swipe anchors.
- **Standard** — Floating dock transitions, filter chip selection, dialog reveals.
- **Delight allowed** — First-run onboarding reveal, monthly financial milestone badge.

## Gestures and haptics
- Velocity carried into release on swipe-to-dismiss.
- Haptics fired on confirmation of transaction deletion and PIN unlock success.

## Reduced motion
- When system Remove Animations is active, all specs resolve to instant zero-duration transitions. Resting state displays complete data without loss of information.

## Not in this codebase
- Directional sliding between top-level tabs (violates T-011).
- Bouncy oscillatory springs on currency or chart numbers (violates T-004).
- Perpetual pulsing or looping ambient animations (violates antislop R-19).
