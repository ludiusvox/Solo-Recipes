/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { 
  Heart, 
  Clock, 
  Utensils, 
  ArrowLeft,
  Check, 
  Search, 
  ChefHat, 
  Flame, 
  Sparkles,
  Award,
  Plus,
  X,
  ImageOff,
  Info,
  Trash2,
  ExternalLink
} from 'lucide-react';
import { RecipeItem, PantryItem } from '../types';

interface RecipesViewProps {
  recipes: RecipeItem[];
  pantry: PantryItem[];
  favorites: string[];
  toggleFavorite: (recipeId: string) => void;
  selectedRecipeId: string | null;
  setSelectedRecipeId: (id: string | null) => void;
  addRecipe: (recipe: RecipeItem) => void;
  removeRecipe: (id: string) => void;
}

const RecipeImage = ({ src, alt, className }: { src: string, alt: string, className?: string }) => {
  const [hasError, setHasError] = useState(false);
  if (hasError || !src) {
    return (
      <div className={`${className} bg-surface-container-high flex flex-col items-center justify-center text-on-surface-variant/40 p-4 text-center`}>
        <ImageOff className="w-10 h-10 mb-2" />
        <span className="text-[10px] font-bold uppercase tracking-tighter">Image Invalid</span>
      </div>
    );
  }
  return <img src={src} alt={alt} referrerPolicy="no-referrer" className={className} onError={() => setHasError(true)} />;
};

export default function RecipesView({ 
  recipes, pantry, favorites, toggleFavorite, selectedRecipeId, setSelectedRecipeId, addRecipe, removeRecipe
}: RecipesViewProps) {
  const [activeCategoryFilter, setActiveCategoryFilter] = useState<'all' | 'beef' | 'seafood' | 'greens' | 'root_veg' | 'poultry'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [isAddingRecipe, setIsAddingRecipe] = useState(false);
  const [checkedIngredients, setCheckedIngredients] = useState<Record<string, boolean>>({});
  const [completedSteps, setCompletedSteps] = useState<Record<number, boolean>>({});
  const [activeStep, setActiveStep] = useState(0);

  const [newRecipe, setNewRecipe] = useState<Partial<RecipeItem>>({
    title: '', category: 'beef', image: '', tagline: '', description: '', nutrition: { calories: '0', protein: '0', carbs: '0', fat: '0' }
  });
  const [rawIngredients, setRawIngredients] = useState('');

  const filteredRecipes = recipes.filter(recipe => {
    const matchesCategory = activeCategoryFilter === 'all' || recipe.category === activeCategoryFilter;
    const matchesSearch = recipe.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          recipe.tagline.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  const selectedRecipe = recipes.find(r => r.id === selectedRecipeId);

  const handleAddRecipeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newRecipe.title) return;
    const parsedIngredients = rawIngredients.split(',').map(i => ({ name: i.trim(), amount: 'to taste' })).filter(i => i.name);
    addRecipe({ ...newRecipe as RecipeItem, id: `custom_${Date.now()}`, ingredients: parsedIngredients, steps: ['Cook and enjoy!'], seasoningsUsed: [], prepTime: '15m', cookTime: '15m', servings: 2, difficulty: 'Medium' });
    setIsAddingRecipe(false);
    setNewRecipe({ title: '', category: 'beef', image: '', tagline: '', description: '', nutrition: { calories: '0', protein: '0', carbs: '0', fat: '0' } });
    setRawIngredients('');
  };

  const isFavorite = (recipeId: string) => favorites.includes(recipeId);

  if (selectedRecipe) {
    return (
      <div id="recipe-expanded-screen" className="animate-fade-in pb-32">
        <div className="relative h-72 w-full">
          <RecipeImage src={selectedRecipe.image} alt={selectedRecipe.title} className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-background via-black/20 to-black/60"></div>
          <div className="absolute top-4 inset-x-0 px-gutter flex items-center justify-between">
            <button onClick={() => setSelectedRecipeId(null)} className="bg-white/80 p-2.5 rounded-full"><ArrowLeft className="w-5 h-5" /></button>
            <button onClick={() => removeRecipe(selectedRecipe.id)} className="bg-error/10 text-error p-2.5 rounded-full hover:bg-error/20 transition-colors"><Trash2 className="w-5 h-5" /></button>
          </div>
          <div className="absolute bottom-6 left-0 right-0 px-gutter">
            <span className="text-[10px] font-bold uppercase px-2 py-0.5 rounded-full bg-primary-container text-on-primary-container">{selectedRecipe.category}</span>
            <h2 className="font-serif text-2xl font-extrabold text-on-surface mt-1">{selectedRecipe.title}</h2>
          </div>
        </div>
        <main className="px-gutter mt-6 space-y-6">
          <p className="text-on-surface-variant leading-relaxed">{selectedRecipe.description}</p>
          <div className="space-y-2">
            <h3 className="font-serif text-lg font-bold">Ingredients</h3>
            <div className="grid gap-2">
              {selectedRecipe.ingredients.map((ing, i) => (
                <div key={i} className="bg-surface-container-low p-3 rounded-xl flex justify-between items-center border border-outline-variant/10">
                  <span className="text-sm font-medium">{ing.name}</span>
                  <span className="text-xs font-mono opacity-60">{ing.amount}</span>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div id="recipe-catalog-screen" className="animate-fade-in pb-32">
      <header className="fixed top-0 left-0 right-0 z-50 bg-surface flex items-center justify-between px-gutter h-16">
        <div className="w-10"></div>
        <h1 className="font-serif text-2xl font-bold text-primary tracking-tight">Signature Recipes</h1>
        <button onClick={() => setIsAddingRecipe(true)} className="text-primary hover:bg-surface-container-high transition-colors p-2 rounded-full"><Plus className="w-6 h-6" /></button>
      </header>

      <main className="pt-20 px-gutter max-w-3xl mx-auto space-y-6">
        <div className="relative flex items-center bg-surface-container-low rounded-full px-4 py-2.5 border border-outline-variant/30">
          <Search className="w-5 h-5 text-on-surface-variant mr-3" />
          <input type="text" placeholder="Search recipes..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="bg-transparent text-sm w-full outline-none" />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredRecipes.map((recipe) => (
            <div key={recipe.id} onClick={() => setSelectedRecipeId(recipe.id)} className="bg-surface-container-lowest rounded-2xl overflow-hidden shadow-sm border border-outline-variant/10 flex flex-col group relative">
              <div className="relative h-44 w-full">
                <RecipeImage src={recipe.image} alt={recipe.title} className="w-full h-full object-cover" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent"></div>
                <button
                  onClick={(e) => { e.stopPropagation(); removeRecipe(recipe.id); }}
                  className="absolute top-3 left-3 p-2 bg-black/40 text-white rounded-full opacity-0 group-hover:opacity-100 transition-opacity backdrop-blur-sm"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
                <button onClick={(e) => { e.stopPropagation(); toggleFavorite(recipe.id); }} className={`absolute top-3 right-3 p-1.5 rounded-full shadow-md ${isFavorite(recipe.id) ? 'bg-rose-500 text-white' : 'bg-white/80 text-on-surface'}`}><Heart className={`w-4 h-4 ${isFavorite(recipe.id) ? 'fill-current' : ''}`} /></button>
              </div>
              <div className="p-4 flex-1">
                <h3 className="font-serif text-lg font-bold text-on-surface leading-snug">{recipe.title}</h3>
                <p className="text-xs text-on-surface-variant line-clamp-2 mt-1">{recipe.description}</p>
              </div>
            </div>
          ))}
        </div>
      </main>

      {isAddingRecipe && (
        <div id="add-recipe-modal" className="fixed inset-0 z-[60] bg-black/70 backdrop-blur-md flex items-center justify-center p-gutter animate-fade-in">
          <div className="bg-surface w-full max-w-lg rounded-3xl overflow-hidden shadow-2xl flex flex-col max-h-[90vh]">
            <div className="px-6 py-4 border-b border-outline-variant/30 flex items-center justify-between bg-primary/5">
              <h2 className="font-serif text-xl font-bold text-primary">Add Recipe</h2>
              <button onClick={() => setIsAddingRecipe(false)} className="p-2 hover:bg-surface-container-high rounded-full"><X className="w-6 h-6" /></button>
            </div>
            <form onSubmit={handleAddRecipeSubmit} className="p-6 space-y-5 overflow-y-auto">
              {/* Educational Alert for Google Photos */}
              <div className="bg-secondary-container/30 p-4 rounded-2xl border border-secondary/20 space-y-3">
                <div className="flex items-center gap-2 text-secondary font-bold text-xs uppercase tracking-widest">
                  <Info className="w-4 h-4" /> Why sharing links fail
                </div>
                <p className="text-[11px] text-on-surface-variant leading-relaxed">
                  Links like <code className="bg-white/50 px-1 rounded">photos.app.goo.gl</code> are <strong>web pages</strong> for people to browse, not direct image files for apps to use.
                </p>
                <div className="bg-white/50 p-3 rounded-xl space-y-2">
                  <p className="text-[11px] font-bold text-primary">How to fix it:</p>
                  <ol className="text-[10px] list-decimal list-inside space-y-1 text-on-surface-variant">
                    <li>Open your Google Photo in a browser.</li>
                    <li>Right-click (or long-press) the image.</li>
                    <li>Select <strong>"Copy image address"</strong>.</li>
                    <li>Paste that link here (it usually starts with <code className="text-primary">lh3.googleusercontent...</code>).</li>
                  </ol>
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-on-surface-variant/70">Recipe Title</label>
                <input required type="text" value={newRecipe.title} onChange={e => setNewRecipe({...newRecipe, title: e.target.value})} className="w-full bg-surface-container-low rounded-xl px-4 py-3 text-sm border border-outline-variant focus:border-primary outline-none" placeholder="Garlic Parmesan Wings" />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-black uppercase text-on-surface-variant/70">Category</label>
                  <select value={newRecipe.category} onChange={e => setNewRecipe({...newRecipe, category: e.target.value as any})} className="w-full bg-surface-container-low rounded-xl px-4 py-3 text-sm border border-outline-variant outline-none">
                    <option value="beef">Beef</option><option value="seafood">Seafood</option><option value="greens">Greens</option><option value="root_veg">Root Veg</option><option value="poultry">Poultry</option>
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-[10px] font-black uppercase text-on-surface-variant/70">Photo Preview</label>
                  <div className="h-[46px] rounded-xl overflow-hidden border border-outline-variant bg-surface-container-high relative">
                    {newRecipe.image ? <RecipeImage src={newRecipe.image} alt="Preview" className="w-full h-full object-cover" /> : <div className="flex items-center justify-center h-full text-[10px] opacity-40 font-bold uppercase tracking-tighter">No Link</div>}
                  </div>
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-on-surface-variant/70">Image Address (lh3...)</label>
                <input type="url" value={newRecipe.image} onChange={e => setNewRecipe({...newRecipe, image: e.target.value})} className="w-full bg-surface-container-low rounded-xl px-4 py-3 text-sm border border-outline-variant outline-none font-mono text-[11px]" placeholder="Paste the 'Copy image address' link here..." />
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-on-surface-variant/70">Ingredients (Commas)</label>
                <textarea value={rawIngredients} onChange={e => setRawIngredients(e.target.value)} className="w-full bg-surface-container-low rounded-xl px-4 py-3 text-sm border border-outline-variant min-h-[80px] outline-none" placeholder="Salt, Pepper, Butter, Chicken..."></textarea>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-on-surface-variant/70">Brief Description</label>
                <textarea value={newRecipe.description} onChange={e => setNewRecipe({...newRecipe, description: e.target.value})} className="w-full bg-surface-container-low rounded-xl px-4 py-3 text-sm border border-outline-variant min-h-[80px] outline-none" placeholder="A family favorite during the summer..."></textarea>
              </div>

              <button type="submit" className="w-full bg-primary text-white font-bold py-4 rounded-2xl shadow-lg active:scale-95 transition-transform flex items-center justify-center gap-2">
                <Sparkles className="w-5 h-5" /> Create Signature Recipe
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
