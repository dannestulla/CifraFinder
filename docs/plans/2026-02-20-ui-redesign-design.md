# UI Redesign: Preview Designs to Production

**Date:** 2026-02-20
**Status:** Approved

## Overview

Implement the preview designs from `ui/preview/` into the existing production screens, replacing the current light theme with a modern dark theme design.

## Approach

**Approach A: Refactor preview components into production**
- Move theme definitions from preview to `ui/theme/`
- Refactor existing screens to use new composables
- Update `CifraFinderTheme` to use dark color scheme
- Replace components with new styled versions

## Design Sections

### 1. Theme Updates

**Color.kt** - New dark theme palette:
- `Background = #121212`
- `Surface = #1E1E1E`
- `Primary = #E55B13` (vibrant orange)
- `TextPrimary = #FFFFFF`
- `TextSecondary = #B3B3B3`

**Theme.kt:**
- Switch from `lightColorScheme` to `darkColorScheme`
- Update status bar to dark

**Add Shapes:**
- Small: 8dp rounded corners
- Medium: 12dp (buttons)
- Large: 20dp (modals, album art)

### 2. Screen Updates

**LoginScreen.kt:**
- Gradient background (vertical: #1A1A2E → #121212 → #0D0D0D)
- Logo: MusicNote icon in 120dp rounded container with shadow
- Title: "Cifra Finder" 32sp bold
- Subtitle: Description text 16sp secondary color
- Login button: Full-width, 56dp height, primary color with shadow

**WhatIsPlayingScreen.kt:**
- Dark background (#121212)
- Header: "Tocando agora" label
- Album art: 280dp with 32dp shadow, 20dp rounded corners
- Song title: 24sp bold, centered
- Artist: 16sp secondary, centered
- Search button: Full-width, 60dp, icon + text, positioned for thumb reach
- Bottom navigation bar instead of FAB

### 3. Components

**BottomNavigationBar.kt (new):**
- 80dp height, Surface background
- Logout icon (left), Home icon (center)
- 48dp touch targets, ripple effect

**LogoutConfirmationDialog:**
- Full-screen overlay (60% black)
- Centered card: Surface color, 20dp corners, 24dp shadow
- Title + subtitle text
- Two buttons: "Não" (outlined), "Sim" (filled primary)

**NormalButton.kt updates:**
- 56-60dp height
- 12dp rounded corners
- Shadow with primary color tint
- Keep loading state

## Files to Modify

1. `ui/theme/Color.kt` - New color palette
2. `ui/theme/Theme.kt` - Dark theme configuration
3. `ui/theme/Type.kt` - Typography updates (if needed)
4. `ui/screens/LoginScreen.kt` - New layout
5. `ui/screens/WhatIsPlayingScreen.kt` - New layout + bottom nav
6. `ui/components/NormalButton.kt` - Styling updates
7. `ui/components/MessageDialog.kt` - Replace with new dialog style
8. `ui/components/BottomNavigationBar.kt` - New component

## Files to Remove/Deprecate

- `ui/components/FAB.kt` - Logout function moved to bottom nav
