/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { 
  Plus, 
  Trash2, 
  Search, 
  Sparkles, 
  CookingPot, 
  Check, 
  Info,
  Layers,
  Leaf,
  Wine,
  Package,
  CalendarDays
} from 'lucide-react';
import { PantryItem } from '../types';
import { RECIPES } from '../data';

interface PantryViewProps {
  pantry: PantryItem[];
  addPantryItem: (name: string, category: PantryItem['category']) => void;
  removePantryItem: (id: string) => void;
  togglePantryItemStock: (name: string) => void;
  onViewRecipe: (id: string) => void;
}

export default function PantryView({
  pantry,
  addPantryItem,
  removePantryItem,
  togglePantryItemStock,
  onViewRecipe
}: PantryViewProps) {
  const [newItemName, setNewItemName] = useState('');
  const [newItemCategory, setNewItemCategory] = useState<PantryItem['category']>('Spices');
  const [pantrySearchQuery, setPantrySearchQuery] = useState('');
  const [showInStockOnly, setShowInStockOnly] = useState(false);
  const [showAddForm, setShowAddForm] = useState(false);

  const handleAddItemSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newItemName.trim()) return;
    addPantryItem(newItemName.trim(), newItemCategory);
    setNewItemName('');
    setShowAddForm(false);
  };

  // Filter pantry list
  const filteredPantry = pantry.filter(item => {
    const matchesSearch = item.name.toLowerCase().includes(pantrySearchQuery.toLowerCase()) ||
                          item.category.toLowerCase().includes(pantrySearchQuery.toLowerCase());
    const matchesStockFilter = !showInStockOnly || item.inStock;
    return matchesSearch && matchesStockFilter;
  });

  // Group pantry by category
  const categories: PantryItem['category'][] = ['Spices', 'Herbs', 'Pantry Basics', 'Sauces & Liquids', 'Fresh'];

  // Calculate matching recipes based on currently in-stock pantry items
  const getSynergeticRecipes = () => {
    const stockedNames = pantry.filter(p => p.inStock).map(p => p.name.toLowerCase());
    
    return RECIPES.map(recipe => {
      const matchedIngredientsCount = recipe.ingredients.filter(ing => {
        const ingName = ing.name.toLowerCase();
        return stockedNames.some(s => ingName.includes(s) || s.includes(ingName));
      }).length;
      
      const matchRatio = matchedIngredientsCount / recipe.ingredients.length;
      return {
        recipe,
        matchedCount: matchedIngredientsCount,
        totalCount: recipe.ingredients.length,
        ratio: matchRatio
      };
    })
    .filter(item => item.matchedCount > 0)
    .sort((a, b) => b.ratio - a.ratio);
  };

  const synergeticRecipes = getSynergeticRecipes();
  const totalStocked = pantry.filter(p => p.inStock).length;

  return (
    <div id="pantry-screen" className="animate-fade-in pb-32">
      <header className="fixed top-0 left-0 right-0 z-50 bg-surface flex items-center justify-between px-gutter h-16 border-b border-transparent shadow-none">
        <div className="w-10"></div>
        <h1 className="font-serif text-2xl font-bold text-primary tracking-tight">
          My Pantry Stock
        </h1>
        <button 
          onClick={() => setShowAddForm(!showAddForm)}
          className="text-primary hover:bg-surface-container-high transition-colors active:scale-95 duration-150 p-2 rounded-full flex items-center justify-center bg-primary/5"
          aria-label="Add Item"
        >
          <Plus className="w-6 h-6" />
        </button>
      </header>

      <main className="pt-20 px-global-padding md:px-gutter max-w-3xl mx-auto space-y-6">
        {/* Intro description */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div className="space-y-1">
            <h2 className="font-serif text-3xl font-bold text-on-surface">Kitchen Inventory</h2>
            <p className="text-sm text-on-surface-variant">
              Manage your seasonings, spices, and base prep elements to automatically match recipes.
            </p>
          </div>
          <div className="bg-primary/5 border border-primary/20 px-4 py-2 rounded-2xl flex items-center gap-2 text-primary font-sans text-sm font-semibold">
            <CalendarDays className="w-4 h-4" /> {totalStocked} Stocked Items
          </div>
        </div>

        {/* Dynamic ADD ITEM Inline Form */}
        {showAddForm && (
          <form 
            onSubmit={handleAddItemSubmit} 
            className="bg-surface-container-low rounded-2xl p-4 border border-primary/20 animate-scale-up space-y-3"
          >
            <h3 className="font-sans text-sm font-bold text-primary flex items-center gap-1.5">
              <Plus className="w-4 h-4" /> Register New Ingredient
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-on-surface-variant mb-1">Ingredient Name</label>
                <input 
                  type="text" 
                  placeholder="e.g. White Pepper, Fresh Thyme..."
                  value={newItemName}
                  onChange={(e) => setNewItemName(e.target.value)}
                  className="w-full bg-surface-container-lowest border border-outline-variant/50 rounded-xl px-3 py-2 text-sm text-on-surface focus:outline-none focus:border-primary font-sans"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-on-surface-variant mb-1">Pantry Category</label>
                <select 
                  value={newItemCategory}
                  onChange={(e) => setNewItemCategory(e.target.value as PantryItem['category'])}
                  className="w-full bg-surface-container-lowest border border-outline-variant/50 rounded-xl px-3 py-2 text-sm text-on-surface focus:outline-none focus:border-primary font-sans"
                >
                  {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-1">
              <button 
                type="button" 
                onClick={() => setShowAddForm(false)}
                className="px-4 py-1.5 text-xs text-on-surface-variant font-medium hover:bg-surface-container-high rounded-full transition-colors"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className="bg-primary text-white text-xs font-bold px-4 py-1.5 rounded-full shadow hover:bg-primary-container transition-colors"
              >
                Add to List
              </button>
            </div>
          </form>
        )}

        {/* Synergy Recommendations Drawer / Section */}
        {synergeticRecipes.length > 0 && (
          <div className="bg-surface-container-low rounded-2xl p-4 border border-outline-variant/20 space-y-3">
            <h3 className="font-sans text-sm font-bold text-primary flex items-center gap-1.5">
              <Sparkles className="w-4 h-4 text-primary fill-primary/20" /> Active Recipes Matched By Pantry
            </h3>
            <div className="flex gap-3 overflow-x-auto pb-1 scrollbar-none">
              {synergeticRecipes.map(({ recipe, matchedCount, totalCount, ratio }) => {
                const percent = Math.round(ratio * 100);
                
                return (
                  <div 
                    key={recipe.id}
                    onClick={() => onViewRecipe(recipe.id)}
                    className="bg-surface-container-lowest border border-outline-variant/10 rounded-xl p-3 min-w-[240px] max-w-[260px] shadow-sm hover:shadow transition-shadow cursor-pointer flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-[9px] font-bold uppercase tracking-wider text-secondary">
                          {recipe.category === 'root_veg' ? 'Root Veg' : recipe.category}
                        </span>
                        <span className="text-[10px] font-bold text-primary bg-primary/5 px-1.5 py-0.5 rounded">
                          {percent}% Match
                        </span>
                      </div>
                      <h4 className="font-serif text-sm font-bold text-on-surface line-clamp-1 mb-1">
                        {recipe.title}
                      </h4>
                      <p className="text-[11px] text-on-surface-variant/80 line-clamp-2 leading-relaxed">
                        You have <strong>{matchedCount}</strong> of <strong>{totalCount}</strong> ingredients.
                      </p>
                    </div>

                    <div className="flex items-center justify-between border-t border-outline-variant/20 pt-2.5 mt-2.5 text-[10px] text-on-surface-variant font-medium">
                      <span>Cook time: {recipe.cookTime}</span>
                      <span className="text-primary hover:underline flex items-center">
                        View recipe <Check className="w-3 h-3 ml-0.5 stroke-[3]" />
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Search and Filters toolbar */}
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1 flex items-center bg-surface-container-low rounded-full px-4 py-2 border border-outline-variant/20">
            <Search className="w-4 h-4 text-on-surface-variant mr-3" />
            <input 
              type="text" 
              placeholder="Search ingredient names, categories..."
              value={pantrySearchQuery}
              onChange={(e) => setPantrySearchQuery(e.target.value)}
              className="bg-transparent text-sm text-on-surface w-full focus:outline-none placeholder-on-surface-variant/70 font-sans"
            />
            {pantrySearchQuery && (
              <button 
                onClick={() => setPantrySearchQuery('')}
                className="text-on-surface-variant text-xs hover:underline pr-1"
              >
                Clear
              </button>
            )}
          </div>

          <button
            onClick={() => setShowInStockOnly(!showInStockOnly)}
            className={`px-4 py-2 rounded-full text-xs font-semibold border whitespace-nowrap transition-all duration-150 ${showInStockOnly ? 'bg-primary border-primary text-white shadow-sm' : 'bg-surface-container-low border-transparent text-on-surface-variant hover:border-outline-variant'}`}
          >
            {showInStockOnly ? 'Showing: In-Stock Only' : 'Filter: Show All Items'}
          </button>
        </div>

        {/* Pantry Category Blocks list */}
        <div className="space-y-6">
          {categories.map((category) => {
            const categoryItems = filteredPantry.filter(item => item.category === category);
            if (categoryItems.length === 0) return null;

            return (
              <div key={category} className="space-y-3">
                <div className="flex items-center gap-2 border-b border-outline-variant/30 pb-1.5">
                  <h3 className="font-serif text-lg font-bold text-on-surface-variant">
                    {category}
                  </h3>
                  <span className="text-xs bg-surface-container-high text-on-surface-variant/80 font-bold px-2 py-0.5 rounded-full">
                    {categoryItems.length}
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-2.5">
                  {categoryItems.map((item) => (
                    <div 
                      key={item.id}
                      onClick={() => togglePantryItemStock(item.name)}
                      className={`flex items-center justify-between p-3 rounded-xl border cursor-pointer transition-all ${item.inStock ? 'bg-surface-container-lowest border-primary/30 shadow-sm' : 'bg-surface-container-low/40 border-transparent opacity-75'}`}
                    >
                      <div className="flex items-center gap-3">
                        <div className={`w-5 h-5 rounded-full flex items-center justify-center border transition-colors ${item.inStock ? 'bg-primary border-primary text-white' : 'border-outline-variant text-transparent'}`}>
                          <Check className="w-3.5 h-3.5 stroke-[3]" />
                        </div>
                        <span className={`font-sans text-sm font-medium ${item.inStock ? 'text-on-surface font-semibold' : 'text-on-surface-variant/60'}`}>
                          {item.name}
                        </span>
                      </div>

                      <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                        <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${item.inStock ? 'bg-primary/10 text-primary' : 'bg-surface-container-high text-on-surface-variant/50'}`}>
                          {item.inStock ? 'In Stock' : 'Out of Stock'}
                        </span>
                        
                        <button 
                          onClick={() => removePantryItem(item.id)}
                          className="p-1.5 text-on-surface-variant/40 hover:text-error hover:bg-error-container/20 rounded-full transition-colors active:scale-90"
                          title="Delete from list"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            );
          })}

          {filteredPantry.length === 0 && (
            <div className="text-center py-12 text-on-surface-variant/70 border-2 border-dashed border-outline-variant/30 rounded-2xl">
              <CookingPot className="w-10 h-10 text-outline-variant/60 mx-auto mb-2" />
              <p className="font-sans text-sm font-medium">No ingredients found matching your search.</p>
              <button 
                onClick={() => { setPantrySearchQuery(''); setShowInStockOnly(false); }}
                className="text-xs text-primary font-bold hover:underline mt-1"
              >
                Clear Filters
              </button>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
