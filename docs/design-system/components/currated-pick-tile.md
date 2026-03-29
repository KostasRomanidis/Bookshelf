# Curated Picks Tile

## Overview

The **Curated Picks Tile** is a high-emphasis hero component displayed on the Home screen to highlight a single featured book.

Its purpose is to draw immediate attention to one recommended title and create visual hierarchy above the standard book list items.

---

## Visual Anatomy

### Container

* Material 3 `Surface`
* Corner radius: **8dp**
* Subtle shadow elevation

### Background

* High-quality book cover image
* Bottom-to-top gradient overlay to improve text legibility

### Decorative Layer

* Optional secondary thematic background image or texture behind the book cover
* Intended to add depth and visual richness
* Should remain subtle and not reduce readability

### Labels

* Text: `DAILY RECOMMENDATION`
* All caps
* Small size
* Medium emphasis

### Title

* Large
* Bold
* High emphasis

### Author

* Medium size
* Regular weight
* High emphasis

---

## Layout & Spacing

* Internal padding: **24dp**
* Bottom margin: **16dp**

---

## Behavior

* Tile is rendered on the Home screen
* Uses static or preview data
* Entire tile is clickable
* Click navigates to the Book Details screen for the featured book

---

## Interaction

* Tapping the tile navigates to Book Details

---

## Content Rules

* Show a single featured book only
* Title should handle overflow gracefully
* Author should handle overflow gracefully
* Gradient overlay must ensure text remains readable on top of bright or complex covers
* Decorative/thematic background must remain secondary to the book content

---

## Accessibility

* Entire tile should be accessible as a single clickable element
* Text contrast must remain readable over the background image
* Content descriptions should be meaningful when an image is shown

---

## Implementation Notes

### UI Phase

* Build a reusable composable for the hero tile
* Keep it stateless
* Accept UI-ready data only
* Use placeholder content until selection logic is added

Suggested UI API:

* `title: String`
* `author: String`
* `coverImageUrl: String?`
* `onClick: () -> Unit`

---

## Related

* Used in:

    * Home Screen

* Depends on:

    * Global design system (`../DESIGN.md`)
