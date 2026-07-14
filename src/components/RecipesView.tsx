/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { 
  Heart, 
  Clock, 
  Utensils, 
  ChevronRight, 
  ArrowLeft, 
  Check, 
  Search, 
  ChefHat, 
  Flame, 
  CookingPot, 
  FlameKindling,
  Sparkles,
  Award
} from 'lucide-react';
import { RECIPES } from '../data';
import { RecipeItem, PantryItem } from '../types';

interface RecipesViewProps {
  pantry: PantryItem[];
  favorites: string[];
  toggleFavorite: (recipeId: string) => void;
  selectedRecipeId: string | null;
  setSelectedRecipeId: (id: string | null) => void;
}

export default function RecipesView({ 
  pantry, 
  favorites, 
  toggleFavorite,
  selectedRecipeId,
  setSelectedRecipeId
}: RecipesViewProps) {
  const [activeCategoryFilter, setActiveCategoryFilter] = useState<'all' | 'beef' | 'seafood' | 'greens' | 'root_veg' | 'poultry'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  
  // Interactive cooking states for expanded recipe view
  const [checkedIngredients, setCheckedIngredients] = useState<Record<string, boolean>>({});
  const [completedSteps, setCompletedSteps] = useState<Record<number, boolean>>({});
  const [activeStep, setActiveStep] = useState(0);

  // Filter recipes
  const filteredRecipes = RECIPES.filter(recipe => {
    const matchesCategory = activeCategoryFilter === 'all' || recipe.category === activeCategoryFilter;
    const matchesSearch = recipe.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          recipe.tagline.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          recipe.ingredients.some(i => i.name.toLowerCase().includes(searchQuery.toLowerCase()));
    return matchesCategory && matchesSearch;
  });

  const selectedRecipe = RECIPES.find(r => r.id === selectedRecipeId);

  // Helper to check if ingredient is currently available in Pantry
  const isIngredientInStock = (name: string): boolean => {
    const cleanName = name.toLowerCase();
    return pantry.some(p => {
      const pName = p.name.toLowerCase();
      return (pName.includes(cleanName) || cleanName.includes(pName)) && p.inStock;
    });
  };

  const handleOpenRecipe = (recipe: RecipeItem) => {
    setSelectedRecipeId(recipe.id);
    setCheckedIngredients({});
    setCompletedSteps({});
    setActiveStep(0);
  };

  const toggleIngredientCheck = (ingName: string) => {
    setCheckedIngredients(prev => ({
      ...prev,
      [ingName]: !prev[ingName]
    }));
  };

  const toggleStepCheck = (index: number) => {
    setCompletedSteps(prev => ({
      ...prev,
      [index]: !prev[index]
    }));
    if (!completedSteps[index] && index === activeStep) {
      setActiveStep(index + 1);
    }
  };

  const isFavorite = (recipeId: string) => favorites.includes(recipeId);

  // RENDER DETAILED VIEW OF SELECTED RECIPE
  if (selectedRecipe) {
    const isFav = isFavorite(selectedRecipe.id);
    const inStockCount = selectedRecipe.ingredients.filter(i => isIngredientInStock(i.name)).length;
    const totalIngredients = selectedRecipe.ingredients.length;
    const percentMatched = Math.round((inStockCount / totalIngredients) * 100);

    return (
      <div id="recipe-expanded-screen" className="animate-fade-in pb-32">
        {/* Transparent Header overlay on beautiful food image */}
        <div className="relative h-72 md:h-96 w-full">
          <img 
            src={selectedRecipe.image} 
            alt={selectedRecipe.title} 
            referrerPolicy="no-referrer"
            className="w-full h-full object-cover"
          />
          {/* Gradients */}
          <div className="absolute inset-0 bg-gradient-to-t from-background via-black/30 to-black/65"></div>
          
          {/* Top floating controls */}
          <div className="absolute top-4 inset-x-0 px-gutter flex items-center justify-between">
            <button 
              id="recipe-back-btn"
              onClick={() => setSelectedRecipeId(null)}
              className="bg-white/80 backdrop-blur-sm text-on-surface hover:bg-white transition-colors p-2.5 rounded-full shadow-md flex items-center justify-center active:scale-95"
              aria-label="Back to list"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <h1 className="text-white font-serif text-lg font-bold shadow-sm hidden md:block">
              {selectedRecipe.title}
            </h1>
            <button 
              onClick={() => toggleFavorite(selectedRecipe.id)}
              className={`backdrop-blur-sm p-2.5 rounded-full shadow-md flex items-center justify-center active:scale-95 transition-colors ${isFav ? 'bg-rose-50 text-rose-500' : 'bg-white/80 text-on-surface hover:bg-white'}`}
              aria-label="Favorite recipe"
            >
              <Heart className={`w-5 h-5 ${isFav ? 'fill-current' : ''}`} />
            </button>
          </div>

          {/* Hero text overlayed at the bottom of image */}
          <div className="absolute bottom-6 left-0 right-0 px-global-padding md:px-gutter">
            <div className="max-w-3xl mx-auto">
              <span className="inline-block text-[11px] font-bold tracking-wider uppercase px-2.5 py-0.5 rounded-full bg-primary-container text-on-primary-container mb-2 shadow-sm">
                {selectedRecipe.category.toUpperCase()} • {selectedRecipe.difficulty}
              </span>
              <h2 className="font-serif text-2xl md:text-4xl font-extrabold text-on-surface tracking-tight leading-tight">
                {selectedRecipe.title}
              </h2>
              <p className="font-sans text-sm text-on-surface-variant font-medium mt-1">
                {selectedRecipe.tagline}
              </p>
            </div>
          </div>
        </div>

        {/* Content Section */}
        <main className="px-global-padding md:px-gutter max-w-3xl mx-auto mt-6 space-y-8">
          {/* Recipe Stats Section */}
          <div className="grid grid-cols-3 gap-2 bg-surface-container-low rounded-2xl p-4 border border-outline-variant/20">
            <div className="text-center">
              <span className="block text-[10px] font-semibold text-on-surface-variant/70 uppercase">Prep Time</span>
              <span className="font-sans text-base font-bold text-primary flex items-center justify-center gap-1 mt-1">
                <Clock className="w-4 h-4" /> {selectedRecipe.prepTime}
              </span>
            </div>
            <div className="text-center border-x border-outline-variant/30">
              <span className="block text-[10px] font-semibold text-on-surface-variant/70 uppercase">Cook Time</span>
              <span className="font-sans text-base font-bold text-primary flex items-center justify-center gap-1 mt-1">
                <Flame className="w-4 h-4" /> {selectedRecipe.cookTime}
              </span>
            </div>
            <div className="text-center">
              <span className="block text-[10px] font-semibold text-on-surface-variant/70 uppercase">Servings</span>
              <span className="font-sans text-base font-bold text-primary flex items-center justify-center gap-1 mt-1">
                <Utensils className="w-4 h-4" /> {selectedRecipe.servings} guests
              </span>
            </div>
          </div>

          {/* Description */}
          <p className="font-sans text-base text-on-surface-variant leading-relaxed">
            {selectedRecipe.description}
          </p>

          {/* Nutrition Chips (Strictly aligned with guidelines) */}
          <div id="nutrition-profile">
            <h3 className="font-serif text-lg font-bold text-on-surface mb-3">Nutrition per serving</h3>
            <div className="flex flex-wrap gap-2">
              <span className="px-3.5 py-1.5 rounded-full bg-secondary text-white text-xs font-semibold tracking-wide shadow-sm flex items-center gap-1.5">
                🔥 Calories: {selectedRecipe.nutrition.calories}
              </span>
              <span className="px-3.5 py-1.5 rounded-full bg-secondary text-white text-xs font-semibold tracking-wide shadow-sm flex items-center gap-1.5">
                🍗 Protein: {selectedRecipe.nutrition.protein}
              </span>
              <span className="px-3.5 py-1.5 rounded-full bg-secondary text-white text-xs font-semibold tracking-wide shadow-sm flex items-center gap-1.5">
                🌾 Carbs: {selectedRecipe.nutrition.carbs}
              </span>
              <span className="px-3.5 py-1.5 rounded-full bg-secondary text-white text-xs font-semibold tracking-wide shadow-sm flex items-center gap-1.5">
                🥑 Fat: {selectedRecipe.nutrition.fat}
              </span>
            </div>
          </div>

          {/* Pantry Synergy Score Bar */}
          <div className="bg-surface-container-low rounded-xl p-4 border border-outline-variant/20">
            <div className="flex items-center justify-between mb-2">
              <span className="font-sans text-sm font-semibold text-on-surface flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-primary" /> Pantry Ingredient Match
              </span>
              <span className="font-sans text-xs font-bold text-primary">{percentMatched}% Stocked</span>
            </div>
            <div className="w-full bg-surface-container-high rounded-full h-2 overflow-hidden">
              <div 
                className="bg-primary h-full rounded-full transition-all duration-500" 
                style={{ width: `${percentMatched}%` }}
              ></div>
            </div>
            <p className="text-xs text-on-surface-variant/80 mt-1.5">
              You currently have <strong>{inStockCount}</strong> of <strong>{totalIngredients}</strong> ingredients in stock in your pantry.
            </p>
          </div>

          {/* Ingredients Section */}
          <div>
            <div className="flex items-center justify-between mb-4 border-b border-outline-variant/30 pb-2">
              <h3 className="font-serif text-xl font-bold text-on-surface">Ingredients Checklist</h3>
              <span className="text-xs text-on-surface-variant font-medium">Tap items as you prep</span>
            </div>

            <div className="space-y-2.5">
              {selectedRecipe.ingredients.map((ing, i) => {
                const inPantry = isIngredientInStock(ing.name);
                const isChecked = !!checkedIngredients[ing.name];

                return (
                  <div 
                    key={i}
                    onClick={() => toggleIngredientCheck(ing.name)}
                    className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer transition-all ${isChecked ? 'bg-surface-container-high/30 border-outline-variant/40' : 'bg-surface-container-lowest hover:border-outline-variant border-transparent shadow-sm'}`}
                  >
                    <div className="flex items-center gap-3">
                      <div className={`w-5 h-5 rounded-md flex items-center justify-center border transition-colors ${isChecked ? 'bg-primary border-primary text-white' : 'border-outline text-transparent'}`}>
                        <Check className="w-3.5 h-3.5 stroke-[3]" />
                      </div>
                      <span className={`font-sans text-sm font-medium ${isChecked ? 'line-through text-on-surface-variant/50' : 'text-on-surface'}`}>
                        {ing.name}
                      </span>
                    </div>
                    
                    <div className="flex items-center gap-2">
                      <span className={`text-xs font-mono font-semibold px-2 py-0.5 rounded-md ${isChecked ? 'text-on-surface-variant/40 bg-transparent' : 'text-primary bg-primary-container/10'}`}>
                        {ing.amount}
                      </span>
                      {inPantry && (
                        <span className="text-[10px] bg-primary/10 text-primary font-bold uppercase tracking-wider px-2 py-0.5 rounded-full">
                          In Stock
                        </span>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Directions Section with progress indicator */}
          <div>
            <div className="flex items-center justify-between mb-4 border-b border-outline-variant/30 pb-2">
              <h3 className="font-serif text-xl font-bold text-on-surface">Step-by-Step Directions</h3>
              <span className="text-xs text-on-surface-variant font-medium">Cross them off to stay aligned</span>
            </div>

            <div className="space-y-4">
              {selectedRecipe.steps.map((step, idx) => {
                const isDone = !!completedSteps[idx];
                const isCurrent = idx === activeStep;

                return (
                  <div 
                    key={idx}
                    id={`recipe-step-${idx}`}
                    onClick={() => toggleStepCheck(idx)}
                    className={`flex gap-4 p-4 rounded-xl border transition-all duration-200 cursor-pointer ${isDone ? 'bg-surface-container-high/20 border-outline-variant/30 opacity-60' : isCurrent ? 'bg-primary-container/5 border-primary shadow-sm' : 'bg-surface-container-lowest border-transparent shadow-sm'}`}
                  >
                    {/* Stepper Progress Indicator */}
                    <div className="flex flex-col items-center">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center font-mono font-bold text-xs transition-colors ${isDone ? 'bg-primary text-white' : isCurrent ? 'bg-primary text-white shadow' : 'bg-surface-container-high text-on-surface-variant'}`}>
                        {isDone ? <Check className="w-4 h-4 stroke-[3]" /> : idx + 1}
                      </div>
                      {idx < selectedRecipe.steps.length - 1 && (
                        <div className={`w-0.5 flex-1 min-h-[40px] ${isDone ? 'bg-primary' : 'bg-outline-variant/30'}`}></div>
                      )}
                    </div>

                    <div className="flex-1 space-y-1 py-0.5">
                      <h4 className={`text-xs font-semibold tracking-wider uppercase ${isDone ? 'text-on-surface-variant/60' : isCurrent ? 'text-primary' : 'text-on-surface-variant'}`}>
                        Step {idx + 1} {isCurrent && '• IN PROGRESS'}
                      </h4>
                      <p className={`font-sans text-sm leading-relaxed ${isDone ? 'line-through text-on-surface-variant/50' : 'text-on-surface'}`}>
                        {step}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* End Cooking celebration block */}
          {Object.keys(completedSteps).length === selectedRecipe.steps.length && (
            <div className="bg-primary/5 rounded-2xl p-6 text-center border-2 border-dashed border-primary animate-scale-up space-y-3">
              <div className="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center mx-auto">
                <ChefHat className="w-6 h-6 text-primary" />
              </div>
              <h4 className="font-serif text-lg font-bold text-primary">Dish Completed!</h4>
              <p className="text-xs text-on-surface-variant/80 max-w-md mx-auto">
                Excellent chefwork on cooking this <strong>{selectedRecipe.title}</strong>! Your flavors are stitched perfectly. Bon appétit!
              </p>
              <button 
                onClick={() => setSelectedRecipeId(null)}
                className="inline-block bg-primary text-white text-xs font-bold px-4 py-2 rounded-full shadow hover:bg-primary-container transition-colors"
              >
                Return to Recipe Catalog
              </button>
            </div>
          )}
        </main>
      </div>
    );
  }

  // DEFAULT CATALOG VIEW (BROWSE RECIPES)
  return (
    <div id="recipe-catalog-screen" className="animate-fade-in pb-32">
      <header className="fixed top-0 left-0 right-0 z-50 bg-surface flex items-center justify-between px-gutter h-16 border-b border-transparent shadow-none">
        <div className="w-10"></div>
        <h1 className="font-serif text-2xl font-bold text-primary tracking-tight">
          Signature Recipes
        </h1>
        <div className="w-10"></div>
      </header>

      <main className="pt-20 px-global-padding md:px-gutter max-w-3xl mx-auto space-y-6">
        <div className="space-y-1">
          <h2 className="font-serif text-3xl font-bold text-on-surface">Culinary Creations</h2>
          <p className="text-sm text-on-surface-variant">Standalone recipes created specifically to pair with our Seasoning Stitcher guides.</p>
        </div>

        {/* Search Bar */}
        <div className="relative flex items-center bg-surface-container-low rounded-full px-4 py-2.5 w-full border border-outline-variant/30">
          <Search className="w-5 h-5 text-on-surface-variant mr-3" />
          <input 
            type="text" 
            placeholder="Search recipes or key ingredients..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="bg-transparent text-sm text-on-surface w-full focus:outline-none placeholder-on-surface-variant/70 font-sans"
          />
          {searchQuery && (
            <button 
              onClick={() => setSearchQuery('')}
              className="text-on-surface-variant text-xs hover:underline pr-1"
            >
              Clear
            </button>
          )}
        </div>

        {/* Category Filters row (Material Chips) */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
          {(['all', 'beef', 'seafood', 'greens', 'root_veg', 'poultry'] as const).map((cat) => {
            const active = activeCategoryFilter === cat;
            const label = cat === 'all' ? 'All' : cat === 'seafood' ? 'Fish & Seafood' : cat === 'root_veg' ? 'Root Veg' : cat === 'greens' ? 'Greens' : cat.charAt(0).toUpperCase() + cat.slice(1);
            
            return (
              <button
                key={cat}
                onClick={() => setActiveCategoryFilter(cat)}
                className={`px-3.5 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all duration-150 border ${active ? 'bg-primary border-primary text-white shadow-sm' : 'bg-surface-container-low border-transparent text-on-surface-variant hover:border-outline-variant'}`}
              >
                {label}
              </button>
            );
          })}
        </div>

        {/* Recipe Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredRecipes.map((recipe) => {
            const isFav = isFavorite(recipe.id);
            const inStockCount = recipe.ingredients.filter(i => isIngredientInStock(i.name)).length;
            const totalCount = recipe.ingredients.length;

            return (
              <div 
                key={recipe.id}
                onClick={() => handleOpenRecipe(recipe)}
                className="bg-surface-container-lowest rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-200 border border-outline-variant/10 cursor-pointer flex flex-col justify-between"
              >
                {/* Image & Favorite button */}
                <div className="relative h-44 w-full">
                  <img 
                    src={recipe.image} 
                    alt={recipe.title} 
                    referrerPolicy="no-referrer"
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent"></div>
                  
                  {/* Category chip on image */}
                  <span className="absolute bottom-3 left-3 text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-primary-container text-on-primary-container shadow-sm">
                    {recipe.category === 'root_veg' ? 'Root Veg' : recipe.category}
                  </span>

                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleFavorite(recipe.id);
                    }}
                    className={`absolute top-3 right-3 p-1.5 rounded-full shadow-md transition-all duration-150 active:scale-90 ${isFav ? 'bg-rose-500 text-white' : 'bg-white/80 text-on-surface hover:bg-white'}`}
                    aria-label="Toggle Favorite"
                  >
                    <Heart className={`w-4 h-4 ${isFav ? 'fill-current' : ''}`} />
                  </button>
                </div>

                {/* Info Area */}
                <div className="p-4 flex-1 flex flex-col justify-between space-y-3">
                  <div className="space-y-1">
                    <h3 className="font-serif text-lg font-bold text-on-surface tracking-tight group-hover:text-primary leading-snug">
                      {recipe.title}
                    </h3>
                    <p className="text-xs text-on-surface-variant leading-relaxed line-clamp-2">
                      {recipe.description}
                    </p>
                  </div>

                  {/* Metadata Row */}
                  <div className="flex items-center justify-between border-t border-outline-variant/30 pt-3 text-xs text-on-surface-variant">
                    <span className="flex items-center gap-1 font-sans font-medium">
                      <Clock className="w-3.5 h-3.5 text-primary" /> {recipe.cookTime}
                    </span>
                    <span className="flex items-center gap-1 font-sans font-medium">
                      <Award className="w-3.5 h-3.5 text-primary" /> {recipe.difficulty}
                    </span>
                    <span className="bg-primary/5 text-primary font-bold px-2 py-0.5 rounded-full text-[10px]">
                      {inStockCount}/{totalCount} Prep Items
                    </span>
                  </div>
                </div>
              </div>
            );
          })}

          {filteredRecipes.length === 0 && (
            <div className="col-span-full text-center py-16 text-on-surface-variant">
              No signature recipes match "{searchQuery}"
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
