# Bookshelf Design System (Android / Compose)

## Overview

Bookshelf uses a warm, editorial-inspired visual system focused on **tonal layering, spacing, and subtle depth** instead of borders and hard separation.

Core principles:

* No hard divider lines
* Separation through spacing and tonal contrast
* Soft rounded shapes
* Warm, readable typography
* Minimal but expressive surfaces

This document defines how the design system is implemented in **Android (Jetpack Compose)**.

---

## Core Principles

### 1. No Divider Lines

* Do not use `Divider()` or 1dp borders for separation
* Use:

    * vertical spacing
    * tonal surface differences
    * grouping

---

### 2. Tonal Layering

Use different surface tones instead of borders.

Hierarchy example:

* `background`
* `surface`
* `surfaceContainer`
* `surfaceContainerHigh`

---

### 3. Soft Depth

* Prefer soft shadows and elevation
* Avoid sharp outlines
* Depth should feel ambient, not directional

---

### 4. Editorial Feel

* Generous spacing
* Clean typography
* Content-first layouts

---

## Units (Android)

* Use **dp** for:

    * spacing
    * padding
    * sizes
    * corner radius
    * elevation

* Use **sp** for:

    * font size
    * letter spacing

---

## Spacing Tokens

```kotlin
object Dimens {
    val spaceXs = 4.dp
    val spaceSm = 8.dp
    val spaceMd = 12.dp
    val spaceLg = 16.dp
    val spaceXl = 24.dp
    val space2xl = 32.dp
    val space3xl = 44.dp
}
```

Usage:

* small gaps: `spaceSm`
* standard padding: `spaceLg`
* section spacing: `spaceXl`–`space2xl`
* large separation (lists): `space2xl`–`space3xl`

---

## Corner Radius

```kotlin
object Radius {
    val sm = 6.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val pill = 999.dp
}
```

Usage:

* cards: `md` or `lg`
* buttons: `lg`
* chips: `pill`

---

## Colors (Compose Mapping)

```kotlin
background = #FEF9F1
surface = #FEF9F1
surfaceContainer = #F3EDE2
surfaceContainerHigh = #E7E1D5
surfaceContainerLow = #F8F3EA

primary = #426089
primaryDim = #36547D

onSurface = #353229
onSurfaceVariant = #625F54

outlineVariant = #B6B2A5
```

Rules:

* Do not rely on borders
* Use surface contrast instead
* Text should always be readable on warm background

---

## Typography (Compose)

```kotlin
val Typography = Typography(
    displayLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.5).sp,
        fontWeight = FontWeight.SemiBold
    ),
    displayMedium = TextStyle(
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.4).sp,
        fontWeight = FontWeight.SemiBold
    ),
    headlineSmall = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
        fontWeight = FontWeight.Medium
    )
)
```

Guidelines:

* Line height ≈ 1.4–1.5x font size
* Slight negative tracking only for display text
* Avoid overly dense text blocks

---

## Elevation / Shadow

Use soft, ambient shadows:

```kotlin
Card(
    elevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp
    )
)
```

Rules:

* Avoid sharp shadows
* Prefer subtle depth
* Do not combine elevation with borders

---

## Components

### Buttons

#### Primary

* Gradient or solid primary color
* No border
* Height: 48.dp minimum
* Padding: horizontal 20.dp
* Radius: `Radius.lg`

#### Secondary

* Background: `surfaceContainerHigh`
* Text color: `primary`
* No outline

---

### Cards

* Background: `surfaceContainer`
* Radius: `md` or `lg`
* No borders
* Use padding inside, spacing outside

---

### List Items

* No dividers
* Use vertical spacing between items:

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceLg))
```

* Optional:

    * Slight background variation for emphasis

---

### Chips / Filters

* Shape: pill
* Background: `surfaceContainerHigh`
* Selected:

    * background = `primary`
    * text = white

---

### Inputs

* Filled style preferred
* Background: `surfaceContainer`
* Optional subtle bottom indicator
* Avoid default outlined Material style unless customized

---

## Glass / Floating Surfaces (Android Adaptation)

Instead of web-style backdrop blur:

* Use semi-transparent surface:

    * `surface.copy(alpha = 0.85f)`
* Combine with soft elevation
* Blur is optional and should only be used if already supported

---

## Layout Rules

* Prefer vertical flow
* Avoid clutter
* Use spacing to define hierarchy
* Keep content readable and centered around text

---

## Compose Implementation Notes

* Always prefer:

    * `Surface`, `Card`, `Box`
    * over manual backgrounds + borders
* Avoid:

    * `Divider()`
* Reuse existing components before creating new ones
* Keep UI consistent across screens

---

## Summary

This design system is built around:

* spacing over lines
* tone over borders
* softness over sharpness
* readability over density

If unsure:

> Choose the simplest, softest, and most readable solution.
