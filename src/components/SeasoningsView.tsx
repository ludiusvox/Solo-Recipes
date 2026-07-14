/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { 
  Flame, 
  Leaf, 
  Sparkles, 
  Droplet, 
  Sun, 
  Snowflake, 
  FlameKindling, 
  Wine, 
  Boxes, 
  CupSoda, 
  Database, 
  TreePine, 
  Soup, 
  Globe, 
  HelpCircle, 
  Compass, 
  Sparkle, 
  Milk, 
  ArrowLeft, 
  MoreVertical, 
  Copy, 
  Check, 
  CookingPot,
  Search,
  Plus,
  Eye
} from 'lucide-react';
import { CATEGORIES } from '../data';
import { CategoryDetail, SeasoningItem, PantryItem } from '../types';

// Map icon name string to Lucide React component
const IconMapper: Record<string, React.ComponentType<{ className?: string }>> = {
  Flame,
  Leaf,
  Sparkles,
  Droplet,
  Sun,
  Snowflake,
  FlameKindling,
  Wine,
  Boxes,
  CupSoda,
  Database,
  TreePine,
  Soup,
  Earth: Globe,
  Nut: HelpCircle,
  Compass,
  Sparkle,
  Milky: Milk,
};

interface SeasoningsViewProps {
  pantry: PantryItem[];
  togglePantryItemStock: (name: string) => void;
  addPantryItemDirect: (name: string) => void;
  onViewRecipe: (recipeId: string) => void;
}

export default function SeasoningsView({ 
  pantry, 
  togglePantryItemStock, 
  addPantryItemDirect,
  onViewRecipe
}: SeasoningsViewProps) {
  const [selectedCategoryId, setSelectedCategoryId] = useState<'beef' | 'seafood' | 'greens' | 'root_veg' | 'poultry' | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [showOptionsDropdown, setShowOptionsDropdown] = useState(false);

  const showToast = (message: string) => {
    setToastMessage(message);
    setTimeout(() => {
      setToastMessage(null);
    }, 2500);
  };

  const handleCopyText = (seasoning: SeasoningItem) => {
    const textToCopy = `${seasoning.name} (${seasoning.profile}): ${seasoning.description}`;
    navigator.clipboard.writeText(textToCopy);
    showToast(`Copied "${seasoning.name}" details to clipboard!`);
  };

  const isItemInPantry = (name: string): boolean => {
    return pantry.some(p => p.name.toLowerCase() === name.toLowerCase() && p.inStock);
  };

  const handlePantryToggle = (seasoning: SeasoningItem) => {
    const exists = pantry.some(p => p.name.toLowerCase() === seasoning.name.toLowerCase());
    if (exists) {
      togglePantryItemStock(seasoning.name);
      const nowInStock = !isItemInPantry(seasoning.name);
      showToast(nowInStock ? `Added ${seasoning.name} to Pantry stock` : `Removed ${seasoning.name} from Pantry stock`);
    } else {
      addPantryItemDirect(seasoning.name);
      showToast(`Saved ${seasoning.name} to your Pantry list!`);
    }
  };

  const selectedCategory = CATEGORIES.find(c => c.id === selectedCategoryId);

  // If inside a category, display the seasoning cards list (Mockup 2 style)
  if (selectedCategory) {
    const filteredSeasonings = selectedCategory.seasonings.filter(s => 
      s.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
      s.profile.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.description.toLowerCase().includes(searchQuery.toLowerCase())
    );

    return (
      <div id="seasonings-detail-screen" className="animate-fade-in">
        {/* Header matching image 2 exactly */}
        <header className="fixed top-0 left-0 right-0 z-50 bg-surface flex items-center justify-between px-gutter h-16 border-b border-transparent shadow-none">
          <button 
            id="back-btn-to-categories"
            onClick={() => { setSelectedCategoryId(null); setSearchQuery(''); }}
            className="text-primary hover:bg-surface-container-high transition-colors active:scale-95 duration-150 p-2 rounded-full flex items-center justify-center"
            aria-label="Back to categories"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>
          
          <h1 className="font-serif text-2xl font-semibold text-primary tracking-tight">
            {selectedCategory.name} Seasonings
          </h1>
          
          <div className="relative">
            <button 
              id="category-options-btn"
              onClick={() => setShowOptionsDropdown(!showOptionsDropdown)}
              className="text-primary hover:bg-surface-container-high transition-colors active:scale-95 duration-150 p-2 rounded-full flex items-center justify-center"
              aria-label="More options"
            >
              <MoreVertical className="w-6 h-6" />
            </button>
            
            {showOptionsDropdown && (
              <div className="absolute right-0 mt-2 w-56 bg-surface-container-lowest rounded-xl shadow-lg py-2 border border-outline-variant z-50 animate-scale-up">
                <button 
                  onClick={() => {
                    // Try to direct to a matching recipe
                    if (selectedCategory.id === 'beef') onViewRecipe('garlic_ribeye');
                    if (selectedCategory.id === 'seafood') onViewRecipe('lemon_dill_salmon');
                    if (selectedCategory.id === 'greens') onViewRecipe('arugula_balsamic_salad');
                    if (selectedCategory.id === 'root_veg') onViewRecipe('roasted_roots');
                    if (selectedCategory.id === 'poultry') onViewRecipe('sage_chicken_breast');
                    setShowOptionsDropdown(false);
                  }}
                  className="w-full text-left px-4 py-2 text-sm text-on-surface hover:bg-surface-container-low transition-colors flex items-center gap-2"
                >
                  <Eye className="w-4 h-4 text-primary" />
                  View Signature Recipe
                </button>
                <button 
                  onClick={() => {
                    selectedCategory.seasonings.forEach(s => {
                      if (!pantry.some(p => p.name.toLowerCase() === s.name.toLowerCase())) {
                        addPantryItemDirect(s.name);
                      }
                    });
                    showToast(`Added all ${selectedCategory.name} spices to Pantry list!`);
                    setShowOptionsDropdown(false);
                  }}
                  className="w-full text-left px-4 py-2 text-sm text-on-surface hover:bg-surface-container-low transition-colors flex items-center gap-2"
                >
                  <Plus className="w-4 h-4 text-primary" />
                  Add All to Pantry
                </button>
              </div>
            )}
          </div>
        </header>

        <main className="pt-20 px-global-padding md:px-gutter max-w-3xl mx-auto space-y-stack-gap pb-32">
          {/* Intro Text */}
          <p className="font-sans text-base text-on-surface-variant font-normal leading-relaxed">
            {selectedCategory.description}
          </p>

          {/* Inline Search Bar */}
          <div className="relative flex items-center bg-surface-container-low rounded-full px-4 py-2 w-full border border-outline-variant/30">
            <Search className="w-5 h-5 text-on-surface-variant mr-3" />
            <input 
              type="text" 
              placeholder={`Search ${selectedCategory.name.toLowerCase()} seasonings...`}
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

          {/* Seasoning List - Bento Grid Style Layout */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-stack-gap">
            {filteredSeasonings.map((seasoning) => {
              const IconComponent = IconMapper[seasoning.iconName] || Flame;
              const inPantry = isItemInPantry(seasoning.name);

              return (
                <div 
                  key={seasoning.id} 
                  id={`seasoning-card-${seasoning.id}`}
                  className="bg-surface-container-lowest rounded-xl p-gutter shadow-[0_2px_8px_rgba(0,0,0,0.08)] flex flex-col justify-between hover:shadow-md transition-shadow duration-200 border border-outline-variant/10"
                >
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex items-center gap-3">
                      <div className={`w-12 h-12 rounded-full ${seasoning.iconBgColor} flex items-center justify-center ${seasoning.iconTextColor}`}>
                        <IconComponent className="w-6 h-6" />
                      </div>
                      <div>
                        <h2 className="font-sans text-lg font-semibold text-on-surface">{seasoning.name}</h2>
                        <p className="font-sans text-sm text-on-surface-variant font-medium">{seasoning.profile}</p>
                      </div>
                    </div>
                  </div>
                  
                  <p className="font-sans text-sm text-on-surface-variant leading-relaxed mb-6 line-clamp-3">
                    {seasoning.description}
                  </p>
                  
                  <div className="flex items-center justify-between border-t border-outline-variant/30 pt-3">
                    {/* Pantry Indicator Tag */}
                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${inPantry ? 'bg-primary/10 text-primary' : 'bg-surface-container-low text-on-surface-variant/60'}`}>
                      {inPantry ? 'In Pantry' : 'Not Stocked'}
                    </span>

                    <div className="flex items-center gap-2">
                      <button 
                        onClick={() => handleCopyText(seasoning)}
                        title="Copy details to clipboard"
                        aria-label="Copy to Clipboard" 
                        className="p-2 text-primary hover:bg-surface-container-high rounded-full transition-colors group flex items-center justify-center"
                      >
                        <Copy className="w-5 h-5 group-hover:scale-110 transition-transform" />
                      </button>
                      
                      <button 
                        onClick={() => handlePantryToggle(seasoning)}
                        title={inPantry ? "Remove from Stock" : "Save to Pantry"}
                        aria-label="Save to Pantry" 
                        className={`p-2 rounded-full transition-colors group flex items-center justify-center ${inPantry ? 'bg-primary/10 text-primary' : 'text-primary hover:bg-surface-container-high'}`}
                      >
                        <CookingPot className={`w-5 h-5 group-hover:scale-110 transition-transform ${inPantry ? 'fill-current' : ''}`} />
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}

            {filteredSeasonings.length === 0 && (
              <div className="col-span-full text-center py-12 text-on-surface-variant">
                No seasonings match "{searchQuery}"
              </div>
            )}
          </div>
        </main>

        {/* Global Floating Toast */}
        {toastMessage && (
          <div className="fixed bottom-24 left-1/2 -translate-x-1/2 z-50 bg-inverse-surface text-inverse-on-surface px-6 py-3 rounded-full shadow-lg text-sm font-medium flex items-center gap-2 animate-slide-up">
            <Check className="w-4 h-4 text-primary-fixed" />
            {toastMessage}
          </div>
        )}
      </div>
    );
  }

  // If category is NOT selected, display categories cards list (Mockup 1 style)
  return (
    <div id="seasonings-stitcher-categories-screen" className="animate-fade-in">
      {/* Header matching image 1 exactly */}
      <header className="fixed top-0 left-0 right-0 z-50 bg-surface flex items-center justify-between px-gutter h-16 border-b border-transparent shadow-none">
        <div className="w-10"></div> {/* Spacer to center title */}
        <h1 className="font-serif text-2xl font-bold text-primary tracking-tight">
          Solo-Recipes
        </h1>
        <button 
          onClick={() => {
            showToast("Tip: Tap any category below to stitch seasonings!");
          }}
          className="text-primary hover:bg-surface-container-high transition-colors active:scale-95 duration-150 p-2 rounded-full flex items-center justify-center"
          aria-label="Info"
        >
          <MoreVertical className="w-6 h-6" />
        </button>
      </header>

      <main className="pt-20 px-global-padding md:px-gutter max-w-2xl mx-auto space-y-6 pb-32">
        {/* Title Block */}
        <div className="space-y-1">
          <h2 className="font-serif text-4xl font-extrabold text-on-surface tracking-tight leading-none mt-4">
            Seasoning Stitcher
          </h2>
          <p className="font-sans text-lg text-on-surface-variant font-medium">
            Select a base ingredient to discover flavor pairings.
          </p>
        </div>

        {/* Categories List (Visual match to Mockup 1) */}
        <div className="space-y-5">
          {CATEGORIES.map((category) => (
            <div 
              key={category.id}
              id={`category-card-${category.id}`}
              onClick={() => setSelectedCategoryId(category.id)}
              className="relative rounded-2xl overflow-hidden cursor-pointer group active:scale-[0.99] transition-transform duration-150 shadow-[0_4px_12px_rgba(0,0,0,0.12)] h-44"
            >
              {/* Background Food Image with referrer policy */}
              <img 
                src={category.image} 
                alt={category.name}
                referrerPolicy="no-referrer"
                className="absolute inset-0 w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 ease-out"
              />
              {/* Gradient Overlay for legibility */}
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/45 to-transparent"></div>

              {/* Card Content positioned bottom-left */}
              <div className="absolute bottom-0 inset-x-0 p-gutter text-white flex flex-col justify-end">
                {category.badge && (
                  <span className="self-start text-[10px] font-bold tracking-wider uppercase px-2 py-0.5 rounded bg-secondary-container text-on-secondary-container mb-1.5 shadow-sm">
                    {category.badge}
                  </span>
                )}
                <h3 className="font-serif text-2xl font-bold tracking-tight text-white mb-0.5 leading-tight">
                  {category.name}
                </h3>
                <p className="font-sans text-xs text-stone-200 font-medium">
                  {category.tagline}
                </p>
              </div>
            </div>
          ))}
        </div>
      </main>

      {/* Global Floating Toast */}
      {toastMessage && (
        <div className="fixed bottom-24 left-1/2 -translate-x-1/2 z-50 bg-inverse-surface text-inverse-on-surface px-6 py-3 rounded-full shadow-lg text-sm font-medium flex items-center gap-2 animate-slide-up">
          <Check className="w-4 h-4 text-primary-fixed" />
          {toastMessage}
        </div>
      )}
    </div>
  );
}
