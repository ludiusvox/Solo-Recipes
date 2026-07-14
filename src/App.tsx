/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import { 
  BookOpen, 
  CookingPot, 
  Sparkles, 
  Settings as SettingsIcon,
  FlameKindling,
  ChefHat
} from 'lucide-react';
import { PantryItem } from './types';
import SeasoningsView from './components/SeasoningsView';
import RecipesView from './components/RecipesView';
import PantryView from './components/PantryView';
import SettingsView from './components/SettingsView';

const SEED_PANTRY: PantryItem[] = [
  { id: 'p_paprika', name: 'Smoked Paprika', category: 'Spices', inStock: true },
  { id: 'p_pepper', name: 'Coarse Black Pepper', category: 'Spices', inStock: true },
  { id: 'p_garlic', name: 'Garlic Powder', category: 'Spices', inStock: true },
  { id: 'p_lemon', name: 'Lemon Zest', category: 'Fresh', inStock: true },
  { id: 'p_salt', name: 'Maldon Sea Salt', category: 'Pantry Basics', inStock: true },
  { id: 'p_thyme', name: 'Fresh Thyme', category: 'Herbs', inStock: true },
  { id: 'p_rosemary', name: 'Fresh Rosemary', category: 'Herbs', inStock: true },
  { id: 'p_balsamic', name: 'Balsamic Glaze', category: 'Sauces & Liquids', inStock: false },
  { id: 'p_maple', name: 'Pure Maple Syrup', category: 'Pantry Basics', inStock: false },
  { id: 'p_sesame', name: 'Toasted Sesame', category: 'Spices', inStock: false },
  { id: 'p_dill', name: 'Fresh Dill', category: 'Herbs', inStock: false },
  { id: 'p_sage', name: 'Rubbed Sage', category: 'Herbs', inStock: false },
  { id: 'p_cumin', name: 'Ground Cumin', category: 'Spices', inStock: false },
];

type TabType = 'recipes' | 'pantry' | 'seasonings' | 'settings';

export default function App() {
  const [activeTab, setActiveTab] = useState<TabType>('seasonings'); // Default to seasonings stitcher category view (Mockup 1)
  
  // App persistent states
  const [pantry, setPantry] = useState<PantryItem[]>(() => {
    const saved = localStorage.getItem('solo_pantry');
    return saved ? JSON.parse(saved) : SEED_PANTRY;
  });

  const [favorites, setFavorites] = useState<string[]>(() => {
    const saved = localStorage.getItem('solo_favorites');
    return saved ? JSON.parse(saved) : ['garlic_ribeye']; // pre-favorite Ribeye!
  });

  const [useCelsius, setUseCelsius] = useState(() => {
    return localStorage.getItem('solo_use_celsius') === 'true';
  });

  const [useMetric, setUseMetric] = useState(() => {
    return localStorage.getItem('solo_use_metric') === 'true';
  });

  // Track if we are inside an expanded detail screen to suppress the bottom nav bar
  const [selectedRecipeId, setSelectedRecipeId] = useState<string | null>(null);
  const [categoryDrilledDown, setCategoryDrilledDown] = useState(false);

  // Sync to localStorage
  useEffect(() => {
    localStorage.setItem('solo_pantry', JSON.stringify(pantry));
  }, [pantry]);

  useEffect(() => {
    localStorage.setItem('solo_favorites', JSON.stringify(favorites));
  }, [favorites]);

  useEffect(() => {
    localStorage.setItem('solo_use_celsius', String(useCelsius));
  }, [useCelsius]);

  useEffect(() => {
    localStorage.setItem('solo_use_metric', String(useMetric));
  }, [useMetric]);

  // Pantry State Modifiers
  const addPantryItem = (name: string, category: PantryItem['category']) => {
    const trimmed = name.trim();
    if (!trimmed) return;
    
    // Check if it already exists
    const exists = pantry.some(p => p.name.toLowerCase() === trimmed.toLowerCase());
    if (exists) {
      // Just toggle its stock to true
      setPantry(prev => prev.map(p => p.name.toLowerCase() === trimmed.toLowerCase() ? { ...p, inStock: true } : p));
      return;
    }

    const newItem: PantryItem = {
      id: `custom_${Date.now()}`,
      name: trimmed,
      category,
      inStock: true
    };
    setPantry(prev => [newItem, ...prev]);
  };

  const addPantryItemDirect = (name: string) => {
    // Determine a fallback category
    let cat: PantryItem['category'] = 'Spices';
    const lower = name.toLowerCase();
    if (lower.includes('fresh') || lower.includes('zest') || lower.includes('lemon')) {
      cat = 'Fresh';
    } else if (lower.includes('rosemary') || lower.includes('thyme') || lower.includes('dill') || lower.includes('sage')) {
      cat = 'Herbs';
    } else if (lower.includes('syrup') || lower.includes('salt') || lower.includes('sugar')) {
      cat = 'Pantry Basics';
    } else if (lower.includes('glaze') || lower.includes('vinegar') || lower.includes('oil') || lower.includes('mustard')) {
      cat = 'Sauces & Liquids';
    }

    addPantryItem(name, cat);
  };

  const removePantryItem = (id: string) => {
    setPantry(prev => prev.filter(p => p.id !== id));
  };

  const togglePantryItemStock = (name: string) => {
    setPantry(prev => prev.map(p => 
      p.name.toLowerCase() === name.toLowerCase() 
        ? { ...p, inStock: !p.inStock } 
        : p
    ));
  };

  const toggleFavorite = (recipeId: string) => {
    setFavorites(prev => 
      prev.includes(recipeId) 
        ? prev.filter(id => id !== recipeId) 
        : [...prev, recipeId]
    );
  };

  const resetAppState = () => {
    setPantry(SEED_PANTRY);
    setFavorites(['garlic_ribeye']);
    setUseCelsius(false);
    setUseMetric(false);
    setSelectedRecipeId(null);
  };

  // Jump straight to recipe detailed expanded view from anywhere
  const handleViewRecipe = (recipeId: string) => {
    setActiveTab('recipes');
    setSelectedRecipeId(recipeId);
  };

  // Callback to detect if SeasoningsView has selected a category (drills down)
  // We can scan the query selector or just rely on state inside App, but we can also detect if the element "#seasonings-detail-screen" is present.
  // Alternatively, let's keep a synchronized state of drill down so we can suppress the bottom navbar!
  // Let's hook a MutationObserver or simply let SeasoningsView tell us if it is drilled down, or we can look up if the selector is active.
  // But a cleaner way is just tracking if a category was clicked or let SeasoningsView handle its back button. Let's look up elements.
  useEffect(() => {
    const checkDrilled = () => {
      const isDrilled = !!document.getElementById('seasonings-detail-screen');
      setCategoryDrilledDown(isDrilled);
    };
    
    // Check initially and set interval for reactive suppression
    const interval = setInterval(checkDrilled, 200);
    return () => clearInterval(interval);
  }, []);

  const shouldSuppressNavbar = selectedRecipeId !== null || categoryDrilledDown;

  return (
    <div className="bg-background text-on-background min-h-screen pb-safe select-none">
      {/* Active Tab View Rendering */}
      <div className="pb-24">
        {activeTab === 'seasonings' && (
          <SeasoningsView 
            pantry={pantry}
            togglePantryItemStock={togglePantryItemStock}
            addPantryItemDirect={addPantryItemDirect}
            onViewRecipe={handleViewRecipe}
          />
        )}
        
        {activeTab === 'recipes' && (
          <RecipesView 
            pantry={pantry}
            favorites={favorites}
            toggleFavorite={toggleFavorite}
            selectedRecipeId={selectedRecipeId}
            setSelectedRecipeId={setSelectedRecipeId}
          />
        )}
        
        {activeTab === 'pantry' && (
          <PantryView 
            pantry={pantry}
            addPantryItem={addPantryItem}
            removePantryItem={removePantryItem}
            togglePantryItemStock={togglePantryItemStock}
            onViewRecipe={handleViewRecipe}
          />
        )}
        
        {activeTab === 'settings' && (
          <SettingsView 
            useCelsius={useCelsius}
            setUseCelsius={setUseCelsius}
            useMetric={useMetric}
            setUseMetric={setUseMetric}
            resetAppState={resetAppState}
          />
        )}
      </div>

      {/* Persistent Elegant Bottom Navigation (Suppressed on sub-detail screens) */}
      {!shouldSuppressNavbar && (
        <nav 
          id="global-bottom-navigation"
          className="fixed bottom-0 inset-x-0 bg-surface-container/95 backdrop-blur-md border-t border-outline-variant/20 py-3 px-gutter flex justify-around items-center z-40 shadow-lg animate-slide-up"
        >
          {/* Recipes Tab Button */}
          <button
            id="tab-btn-recipes"
            onClick={() => setActiveTab('recipes')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'recipes' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}
          >
            {activeTab === 'recipes' ? (
              <div className="bg-primary text-white px-5 py-1.5 rounded-full flex items-center justify-center">
                <BookOpen className="w-5 h-5" />
              </div>
            ) : (
              <BookOpen className="w-5 h-5 text-on-surface-variant" />
            )}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'recipes' ? 'text-primary' : 'text-on-surface-variant/80'}`}>
              Recipes
            </span>
          </button>

          {/* Pantry Tab Button */}
          <button
            id="tab-btn-pantry"
            onClick={() => setActiveTab('pantry')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'pantry' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}
          >
            {activeTab === 'pantry' ? (
              <div className="bg-primary text-white px-5 py-1.5 rounded-full flex items-center justify-center">
                <CookingPot className="w-5 h-5" />
              </div>
            ) : (
              <CookingPot className="w-5 h-5 text-on-surface-variant" />
            )}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'pantry' ? 'text-primary' : 'text-on-surface-variant/80'}`}>
              Pantry
            </span>
          </button>

          {/* Seasonings Tab Button (Visual highlight matching Mockup 1 exactly!) */}
          <button
            id="tab-btn-seasonings"
            onClick={() => setActiveTab('seasonings')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'seasonings' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}
          >
            {activeTab === 'seasonings' ? (
              <div className="bg-primary text-white px-6 py-1.5 rounded-full flex items-center justify-center shadow-md">
                <Sparkles className="w-5 h-5" />
              </div>
            ) : (
              <Sparkles className="w-5 h-5 text-on-surface-variant" />
            )}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'seasonings' ? 'text-primary' : 'text-on-surface-variant/80'}`}>
              Seasonings
            </span>
          </button>

          {/* Settings Tab Button */}
          <button
            id="tab-btn-settings"
            onClick={() => setActiveTab('settings')}
            className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'settings' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}
          >
            {activeTab === 'settings' ? (
              <div className="bg-primary text-white px-5 py-1.5 rounded-full flex items-center justify-center">
                <SettingsIcon className="w-5 h-5" />
              </div>
            ) : (
              <SettingsIcon className="w-5 h-5 text-on-surface-variant" />
            )}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'settings' ? 'text-primary' : 'text-on-surface-variant/80'}`}>
              Settings
            </span>
          </button>
        </nav>
      )}
    </div>
  );
}
