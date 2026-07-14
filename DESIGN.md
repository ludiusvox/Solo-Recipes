# Solo-Recipes: Seasoning Stitcher Design System

## Project Context
Solo-Recipes is an AI-powered culinary companion. The "Seasoning Stitcher" feature allows users to select a food category (Meats, Vegetables, etc.) and instantly see a curated list of essential seasonings, herbs, and spices recommended for that food.

## Design Vision
- **Vibe**: Modern, clean, and organic. It should feel like a high-end digital cookbook.
- **Tone**: Professional, encouraging, and "tasty."
- **Visual Style**: High-fidelity Material 3, rounded corners (16dp+), subtle shadows, and plenty of whitespace.

## Design Tokens
### Colors
- **Primary**: #2E7D32 (Forest Green - fresh and organic)
- **Secondary**: #795548 (Warm Earth - representing spices and wood)
- **Background**: #FAFAFA (Clean off-white)
- **Surface**: #FFFFFF
- **Accent**: #FFAB00 (Amber - for "pop" on buttons or highlights)

### Typography
- **Headlines**: Playfair Display or a modern Serif (for that cookbook feel)
- **Body**: Inter or Roboto (for high readability)

## Key Screens to Generate
1. **Category Selection Dashboard**:
   - A grid or list of cards.
   - Each card represents a food category (e.g., "Beef", "Fish & Seafood", "Leafy Greens").
   - Cards should have a subtle earthy background or high-quality food photography.
   - Large, clear typography.

2. **Seasoning Guide (Results)**:
   - A vertical list of seasonings.
   - Each item is a high-fidelity card.
   - Include a "Copy to Clipboard" or "Save to Pantry" action icon.
   - Use a clear hierarchy: Spice Name -> Brief Usage Note.

3. **Global Navigation**:
   - Simple TopAppBar with "Solo-Recipes" branding.
   - Back navigation when a category is selected.

## Component Guardrails
- Use **Material 3 ListItem** for the category items.
- Use **Material 3 Cards** with an elevation of 2dp.
- Apply a **12dp padding** globally for consistent spacing.
- Icons should be **Outlined Material Icons**.
