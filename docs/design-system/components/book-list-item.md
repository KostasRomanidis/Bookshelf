# Book List Item

## Overview

The **Book List Item** is the primary reusable component used across the Bookshelf app for discovery and collection feeds.

It displays a book’s cover, title, author, optional metadata (subjects/categories), and a favorite action.

---

## Visual Anatomy

### Container

* Material 3 `Card` or `Surface`
* Corner radius: **8dp**

### Cover Image

* Aspect ratio: **3:4**
* Displays book cover or placeholder

### Typography

* **Title**

    * Font: Inter
    * Weight: SemiBold
    * Size: 16sp
    * Color: `#353229` (high emphasis)

* **Author**

    * Font: Inter
    * Weight: Regular
    * Size: 14sp
    * Color: `#625f54` (medium emphasis)

* **Metadata Chips**

    * Small tonal chips
    * Background: subtle (theme surface variant)
    * Text color: `#426089`

### Actions

* Favorite button (heart icon)
* Uses Material 3 `IconButton`

---

## Layout & Spacing

* Internal padding: **12dp**
* Horizontal gap (cover → text): **16dp**

### Vertical spacing

* Title → Author: **4dp**
* Author → Chips: **8dp**

---

## States

### Enabled (Default)

* Standard appearance

### Pressed

* Material ripple effect (default M3 behavior)

### Favorited

* Heart icon is filled
* Color: `#426089`

---

## Interaction

* **Card click**

    * Navigates to Book Details screen

* **Favorite icon click**

    * Toggles favorite state locally

---

## Content Rules

* Title:

    * Max lines: 2
    * Ellipsis if overflow

* Author:

    * Max lines: 1
    * Ellipsis if overflow

* Metadata Chips:

    * Show **maximum 2 items**
    * Hide section if no data available

* Cover Image:

    * Show placeholder if no image is available

---

## Accessibility

* Entire card should be accessible as a single clickable element
* Favorite button should have a content description:

    * "Add to favorites" / "Remove from favorites"

---

## Implementation Notes

* Component must be **stateless**

* Accept only UI-ready data:

    * `title: String`
    * `author: String`
    * `subjects: List<String>`
    * `isFavorite: Boolean`
    * `coverImageUrl: String?`
    * `onClick: () -> Unit`
    * `onFavoriteClick: () -> Unit`

* Should be optimized for use inside `LazyColumn`

* Maintain consistent alignment even when chips are missing

* Prefer design system tokens over hardcoded values

---

## Related

* Used in:

    * Home screen

* Depends on:

    * Global design system (`../DESIGN.md`)
