
# AstroKit (KMP) ✨
Offline-first astrology + life-math app built with **Kotlin Multiplatform (KMP)** + **Compose Multiplatform**.

AstroKit currently includes:
- **Zodiac (Sun sign)** from DOB (Western/tropical date ranges)
- **Life Path** + **Personal Year** (numerology)
- **Compatibility** (element/modality based explanation)
- **Profiles** (multi-profile switch + local persistence)
- **Kundli (North Indian chart preview)** with **clickable houses** + planet details (Phase-2)
- **Settings** (Rate/Share/Export profile text, About, app version/build via expect/actual)

> Privacy-first: user profiles + birth details are stored **locally on device** (no backend yet).

---

## Screenshots
Add screenshots here once you’re happy with the UI.

- `docs/screenshots/home.png`
- `docs/screenshots/settings.png`
- `docs/screenshots/kundli.png`

---

## Tech Stack
- **Kotlin 2.x**
- **Kotlin Multiplatform**
- **Compose Multiplatform**
- **Material 3**
- **multiplatform-settings** (local storage)
- Offline “engine” modules for:
    - Zodiac calculator
    - Numerology
    - Kundli preview engine + catalogs (house/planet meanings)

---

## Project Structure (important)
This repo uses the newer KMP template layout:

- `composeApp/`
    - `src/commonMain/` → shared UI + shared logic
    - `src/androidMain/` → Android-specific bits
    - `src/iosMain/` → iOS-specific bits (expect/actual)
- `iosApp/` → iOS runner (Xcode project)
- `gradle/`, `build.gradle.kts`, `settings.gradle.kts`, etc.

> Most work happens inside `composeApp/src/commonMain`.

---

## How Zodiac is Calculated (Sun Sign)
AstroKit computes **Sun sign** from DOB using the standard Western (tropical) date ranges:
- Aries: Mar 21 – Apr 19
- Taurus: Apr 20 – May 20
- Gemini: May 21 – Jun 20
- Cancer: Jun 21 – Jul 22
- Leo: Jul 23 – Aug 22
- Virgo: Aug 23 – Sep 22
- Libra: Sep 23 – Oct 22
- Scorpio: Oct 23 – Nov 21
- Sagittarius: Nov 22 – Dec 21
- Capricorn: Dec 22 – Jan 19
- Aquarius: Jan 20 – Feb 18
- Pisces: Feb 19 – Mar 20

This is deterministic and not random.

---

## How Compatibility is Calculated
Compatibility in Phase-1 is **rule-based** and offline:
- Uses **elements** (Fire/Earth/Air/Water) and **modalities** (Cardinal/Fixed/Mutable)
- Produces a score + “Why this match?” explanation

This is a simplified model intended for an MVP and will evolve in later phases.

---

## Kundli (North Indian) – Current Status
The Kundli feature in Phase-2 is a **preview**:
- Collects **DOB + time + place**
- Generates:
    - Lagna sign (offline logic for now)
    - Houses 1..12 display in North Indian chart layout
    - Planets list and placements (offline stub/engine)
- UI supports:
    - **Clickable houses**
    - Lagna pulse highlight
    - Planet legend row
    - Planet + house detail bottom sheets
    - AI explanation stubs (disabled for now)

**Future plan:** accurate planetary degrees using ephemeris / Vedic calculations + premium LLM reading.

---

## Run on Android
### Option A: Android Studio
1. Open the project in Android Studio
2. Select the **composeApp** configuration
3. Run on emulator/device

### Option B: CLI
```bash
./gradlew :composeApp:installDebug
