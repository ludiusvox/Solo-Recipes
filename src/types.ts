/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export interface SeasoningItem {
  id: string;
  name: string;
  category: 'beef' | 'seafood' | 'greens' | 'root_veg' | 'poultry';
  profile: string;
  description: string;
  iconName: string; // Lucide icon name
  badgeText?: string;
  iconBgColor: string;
  iconTextColor: string;
}

export interface PantryItem {
  id: string;
  name: string;
  category: 'Spices' | 'Herbs' | 'Pantry Basics' | 'Sauces & Liquids' | 'Fresh';
  inStock: boolean;
  notes?: string;
}

export interface RecipeItem {
  id: string;
  title: string;
  tagline: string;
  category: 'beef' | 'seafood' | 'greens' | 'root_veg' | 'poultry';
  prepTime: string;
  cookTime: string;
  servings: number;
  difficulty: 'Easy' | 'Medium' | 'Chef';
  image: string;
  description: string;
  ingredients: { name: string; amount: string }[];
  seasoningsUsed: string[]; // references to SeasoningItem.name or id
  steps: string[];
  nutrition: {
    calories: string;
    protein: string;
    carbs: string;
    fat: string;
  };
}

export interface CategoryDetail {
  id: 'beef' | 'seafood' | 'greens' | 'root_veg' | 'poultry';
  name: string;
  tagline: string;
  description: string;
  badge: string;
  image: string;
  seasonings: SeasoningItem[];
}
