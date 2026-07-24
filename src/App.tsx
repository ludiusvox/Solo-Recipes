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
} from 'lucide-react';
import { PantryItem, RecipeItem } from './types';
import { RECIPES } from './data';
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
  const [activeTab, setActiveTab] = useState<TabType>('seasonings');
  
  const [pantry, setPantry] = useState<PantryItem[]>(() => {
    const saved = localStorage.getItem('solo_pantry');
    return saved ? JSON.parse(saved) : SEED_PANTRY;
  });

  const [recipes, setRecipes] = useState<RecipeItem[]>(() => {
    const saved = localStorage.getItem('solo_recipes');
    return saved ? JSON.parse(saved) : RECIPES;
  });

  const [favorites, setFavorites] = useState<string[]>(() => {
    const saved = localStorage.getItem('solo_favorites');
    return saved ? JSON.parse(saved) : ['garlic_ribeye'];
  });

  const [useCelsius, setUseCelsius] = useState(() => {
    return localStorage.getItem('solo_use_celsius') === 'true';
  });

  const [useMetric, setUseMetric] = useState(() => {
    return localStorage.getItem('solo_use_metric') === 'true';
  });

  const [selectedRecipeId, setSelectedRecipeId] = useState<string | null>(null);
  const [categoryDrilledDown, setCategoryDrilledDown] = useState(false);

  useEffect(() => {
    localStorage.setItem('solo_pantry', JSON.stringify(pantry));
  }, [pantry]);

  useEffect(() => {
    localStorage.setItem('solo_recipes', JSON.stringify(recipes));
  }, [recipes]);

  useEffect(() => {
    localStorage.setItem('solo_favorites', JSON.stringify(favorites));
  }, [favorites]);

  useEffect(() => {
    localStorage.setItem('solo_use_celsius', String(useCelsius));
  }, [useCelsius]);

  useEffect(() => {
    localStorage.setItem('solo_use_metric', String(useMetric));
  }, [useMetric]);

  const addPantryItem = (name: string, category: PantryItem['category']) => {
    const trimmed = name.trim();
    if (!trimmed) return;
    const exists = pantry.some(p => p.name.toLowerCase() === trimmed.toLowerCase());
    if (exists) {
      setPantry(prev => prev.map(p => p.name.toLowerCase() === trimmed.toLowerCase() ? { ...p, inStock: true } : p));
      return;
    }
    const newItem: PantryItem = { id: `custom_${Date.now()}`, name: trimmed, category, inStock: true };
    setPantry(prev => [newItem, ...prev]);
  };

  const addPantryItemDirect = (name: string) => {
    let cat: PantryItem['category'] = 'Spices';
    const lower = name.toLowerCase();
    if (lower.includes('fresh') || lower.includes('zest') || lower.includes('lemon')) cat = 'Fresh';
    else if (lower.includes('rosemary') || lower.includes('thyme') || lower.includes('dill') || lower.includes('sage')) cat = 'Herbs';
    else if (lower.includes('syrup') || lower.includes('salt') || lower.includes('sugar')) cat = 'Pantry Basics';
    else if (lower.includes('glaze') || lower.includes('vinegar') || lower.includes('oil') || lower.includes('mustard')) cat = 'Sauces & Liquids';
    addPantryItem(name, cat);
  };

  const removePantryItem = (id: string) => setPantry(prev => prev.filter(p => p.id !== id));

  const togglePantryItemStock = (name: string) => {
    setPantry(prev => prev.map(p => p.name.toLowerCase() === name.toLowerCase() ? { ...p, inStock: !p.inStock } : p));
  };

  const toggleFavorite = (recipeId: string) => {
    setFavorites(prev => prev.includes(recipeId) ? prev.filter(id => id !== recipeId) : [...prev, recipeId]);
  };

  const addRecipe = (newRecipe: RecipeItem) => setRecipes(prev => [newRecipe, ...prev]);

  const removeRecipe = (id: string) => {
    setRecipes(prev => prev.filter(r => r.id !== id));
    if (selectedRecipeId === id) setSelectedRecipeId(null);
  };

  const resetAppState = () => {
    setPantry(SEED_PANTRY);
    setRecipes(RECIPES);
    setFavorites(['garlic_ribeye']);
    setUseCelsius(false);
    setUseMetric(false);
    setSelectedRecipeId(null);
  };

  const handleViewRecipe = (recipeId: string) => {
    setActiveTab('recipes');
    setSelectedRecipeId(recipeId);
  };

  useEffect(() => {
    const checkDrilled = () => {
      const isDrilled = !!document.getElementById('seasonings-detail-screen') || !!document.getElementById('add-recipe-modal');
      setCategoryDrilledDown(isDrilled);
    };
    const interval = setInterval(checkDrilled, 200);
    return () => clearInterval(interval);
  }, []);

  const shouldSuppressNavbar = selectedRecipeId !== null || categoryDrilledDown;

  return (
    <div className="bg-background text-on-background min-h-screen pb-safe select-none">
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
            recipes={recipes}
            pantry={pantry}
            favorites={favorites}
            toggleFavorite={toggleFavorite}
            selectedRecipeId={selectedRecipeId}
            setSelectedRecipeId={setSelectedRecipeId}
            addRecipe={addRecipe}
            removeRecipe={removeRecipe}
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

      {!shouldSuppressNavbar && (
        <nav 
          id="global-bottom-navigation"
          className="fixed bottom-0 inset-x-0 bg-surface-container/95 backdrop-blur-md border-t border-outline-variant/20 py-3 px-gutter flex justify-around items-center z-40 shadow-lg animate-slide-up"
        >
          <button onClick={() => setActiveTab('recipes')} className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'recipes' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}>
            {activeTab === 'recipes' ? <div className="bg-primary text-white px-5 py-1.5 rounded-full flex items-center justify-center"><BookOpen className="w-5 h-5" /></div> : <BookOpen className="w-5 h-5 text-on-surface-variant" />}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'recipes' ? 'text-primary' : 'text-on-surface-variant/80'}`}>Recipes</span>
          </button>
          <button onClick={() => setActiveTab('pantry')} className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'pantry' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}>
            {activeTab === 'pantry' ? <div className="bg-primary text-white px-5 py-1.5 rounded-full flex items-center justify-center"><CookingPot className="w-5 h-5" /></div> : <CookingPot className="w-5 h-5 text-on-surface-variant" />}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'pantry' ? 'text-primary' : 'text-on-surface-variant/80'}`}>Pantry</span>
          </button>
          <button onClick={() => setActiveTab('seasonings')} className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'seasonings' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}>
            {activeTab === 'seasonings' ? <div className="bg-primary text-white px-6 py-1.5 rounded-full flex items-center justify-center shadow-md"><Sparkles className="w-5 h-5" /></div> : <Sparkles className="w-5 h-5 text-on-surface-variant" />}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'seasonings' ? 'text-primary' : 'text-on-surface-variant/80'}`}>Seasonings</span>
          </button>
          <button onClick={() => setActiveTab('settings')} className={`flex flex-col items-center gap-1 transition-all ${activeTab === 'settings' ? 'scale-105' : 'opacity-70 hover:opacity-100'}`}>
            {activeTab === 'settings' ? <div className="bg-primary text-white px-5 py-1.5 rounded-full flex items-center justify-center"><SettingsIcon className="w-5 h-5" /></div> : <SettingsIcon className="w-5 h-5 text-on-surface-variant" />}
            <span className={`text-[11px] font-sans font-bold tracking-wide ${activeTab === 'settings' ? 'text-primary' : 'text-on-surface-variant/80'}`}>Settings</span>
          </button>
        </nav>
      )}
    </div>
  );
}
