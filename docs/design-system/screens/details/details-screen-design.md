# Book Details Screen Design (Android / Compose)

## Goal

A warm, editorial-style book details screen focused on readability, hierarchy, and soft visual depth.

The screen should feel:

* calm
* spacious
* content-first

---

## Structure

The screen is vertically structured in this order:

1. Top app bar
2. Hero section (book cover)
3. Title and author
4. Metadata row
5. Copyright / status
6. Primary action button
7. Subjects (chips)
8. Summary section
9. Formats section
10. Librarian note

---

## Visual Principles

* No divider lines
* Use spacing and tonal surfaces for separation
* Warm background (#FEF9F1)
* Soft shadows for elevation
* Rounded containers
* Inter font across all content

---

## Sections

### Top App Bar

* Back button (left)
* Title centered or left-aligned
* Action icons (favorite, share)
* No elevation or minimal elevation

---

### Hero Section (Book Cover)

* Large rounded container
* Background: `surfaceContainer`
* Padding around image
* Book cover centered
* Strong but soft shadow under image

Layout intent:

* This is the main visual anchor
* Should feel elevated but soft

---

### Title Block

Includes:

* Category label (small caps / label style)
* Book title (display style)
* Author (body style)

Guidelines:

* Title is the most prominent text
* Author is secondary
* Use vertical spacing between elements

---

### Metadata Row

* Two cards side by side:

    * Downloads
    * Language

Style:

* Rounded cards
* Background: `surfaceContainerHigh`
* Icon + label + value

---

### Copyright Section

* Single card
* Centered content
* Icon + label (“Public Domain”)

---

### Primary Action

* Full-width button
* Text: “Start Reading”
* Style:

    * Primary color background
    * Rounded corners (large radius)
    * Height: ~48–56.dp

---

### Subjects (Chips)

* Horizontal/flow layout
* Pill-shaped chips
* Background: `surfaceContainerHigh`

Selected state (if needed):

* Primary background
* White text

---

### Summary Section

* Title: “SUMMARY”
* Paragraph text

Guidelines:

* Comfortable line height (~1.5x)
* Generous spacing
* Avoid dense blocks

---

### Formats Section

* Card container
* Title: “AVAILABLE FORMATS”

Each row:

* Left: icon
* Center: label + metadata
* Right: action icon (download / open)

Rules:

* No separators
* Use vertical spacing
* Use subtle tonal contrast

---

### Librarian Note

* Card container
* Slightly different tone
* Italic or styled text

Floating action button:

* Bottom-right of card
* Small circular button
* Soft elevation

---

## Spacing

* Screen horizontal padding: 16.dp
* Section spacing: 24–32.dp
* Card internal padding: 16.dp
* Large visual separation between major sections

---

## Typography

* Font: Inter
* Display: SemiBold
* Headline: Medium
* Body: Regular
* Label: Medium

Hierarchy:

* Title > Author > Metadata > Body

---

## Android / Compose Notes

* Use dp for spacing and radius
* Use sp for text and letter spacing
* Use `LazyColumn` with vertical spacing instead of dividers
* Use `Surface`, `Card`, and `Box` for layout
* Reuse existing components where possible
* Do not change logic or navigation

---

## Behavior Constraints

* Preserve existing navigation
* Preserve paging/state logic
* UI-only changes unless minimal wiring is needed
