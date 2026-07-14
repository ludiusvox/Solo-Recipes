/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { 
  Settings, 
  BookOpen, 
  RotateCcw, 
  Flame, 
  Award, 
  ShieldCheck, 
  Scale, 
  Thermometer, 
  Info,
  ChevronRight,
  Sparkles
} from 'lucide-react';

interface SettingsViewProps {
  useCelsius: boolean;
  setUseCelsius: (val: boolean) => void;
  useMetric: boolean;
  setUseMetric: (val: boolean) => void;
  resetAppState: () => void;
}

export default function SettingsView({
  useCelsius,
  setUseCelsius,
  useMetric,
  setUseMetric,
  resetAppState
}: SettingsViewProps) {
  
  const handleResetClick = () => {
    if (window.confirm("Are you sure you want to reset your pantry list and saved favorites? This action is irreversible.")) {
      resetAppState();
      alert("App state successfully reset to factory default organic pairings!");
    }
  };

  return (
    <div id="settings-screen" className="animate-fade-in pb-32">
      <header className="fixed top-0 left-0 right-0 z-50 bg-surface flex items-center justify-between px-gutter h-16 border-b border-transparent shadow-none">
        <div className="w-10"></div>
        <h1 className="font-serif text-2xl font-bold text-primary tracking-tight">
          App Settings
        </h1>
        <div className="w-10"></div>
      </header>

      <main className="pt-20 px-global-padding md:px-gutter max-w-3xl mx-auto space-y-6">
        <div className="space-y-1">
          <h2 className="font-serif text-3xl font-bold text-on-surface">Configuration</h2>
          <p className="text-sm text-on-surface-variant">Calibrate cooking preferences, measurements, and read about the organic design architecture.</p>
        </div>

        {/* Cooking Unit toggles card */}
        <div className="bg-surface-container-lowest rounded-2xl p-gutter shadow-sm border border-outline-variant/10 space-y-4">
          <h3 className="font-sans text-sm font-bold text-primary flex items-center gap-1.5 uppercase tracking-wider">
            <Scale className="w-4 h-4 text-primary" /> Culinary Units & Scaling
          </h3>

          {/* Temperature Toggle */}
          <div className="flex items-center justify-between py-2 border-b border-outline-variant/30">
            <div className="space-y-0.5">
              <span className="block font-sans text-sm font-semibold text-on-surface flex items-center gap-1.5">
                <Thermometer className="w-4 h-4 text-secondary" /> Temperature Standard
              </span>
              <span className="block text-xs text-on-surface-variant">Use Fahrenheit or Celsius for step indicators</span>
            </div>
            
            <div className="flex items-center bg-surface-container-low p-1 rounded-full border border-outline-variant/20">
              <button
                type="button"
                onClick={() => setUseCelsius(false)}
                className={`px-3 py-1 rounded-full text-xs font-bold transition-all ${!useCelsius ? 'bg-primary text-white shadow-sm' : 'text-on-surface-variant'}`}
              >
                °F
              </button>
              <button
                type="button"
                onClick={() => setUseCelsius(true)}
                className={`px-3 py-1 rounded-full text-xs font-bold transition-all ${useCelsius ? 'bg-primary text-white shadow-sm' : 'text-on-surface-variant'}`}
              >
                °C
              </button>
            </div>
          </div>

          {/* Measurement System Toggle */}
          <div className="flex items-center justify-between py-2 border-b border-outline-variant/30">
            <div className="space-y-0.5">
              <span className="block font-sans text-sm font-semibold text-on-surface flex items-center gap-1.5">
                <Scale className="w-4 h-4 text-secondary" /> Ingredient Measurements
              </span>
              <span className="block text-xs text-on-surface-variant">Display imperial ounces/pounds or metric grams/milliliters</span>
            </div>

            <div className="flex items-center bg-surface-container-low p-1 rounded-full border border-outline-variant/20">
              <button
                type="button"
                onClick={() => setUseMetric(false)}
                className={`px-3.5 py-1 rounded-full text-xs font-bold transition-all ${!useMetric ? 'bg-primary text-white shadow-sm' : 'text-on-surface-variant'}`}
              >
                Imperial
              </button>
              <button
                type="button"
                onClick={() => setUseMetric(true)}
                className={`px-3.5 py-1 rounded-full text-xs font-bold transition-all ${useMetric ? 'bg-primary text-white shadow-sm' : 'text-on-surface-variant'}`}
              >
                Metric
              </button>
            </div>
          </div>

          <div className="text-[11px] text-on-surface-variant/80 flex items-start gap-1.5 leading-normal">
            <Info className="w-3.5 h-3.5 text-primary shrink-0 mt-0.5" />
            Changing these units will automatically convert standard dry-rub values and pre-set water boiling benchmarks inside our recipes in future sessions.
          </div>
        </div>

        {/* Design architecture block */}
        <div className="bg-surface-container-low rounded-2xl p-gutter space-y-4 border border-outline-variant/20">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <BookOpen className="w-4 h-4" />
            </div>
            <h3 className="font-serif text-lg font-bold text-on-surface">Design Architecture</h3>
          </div>

          <div className="space-y-3 font-sans text-sm text-on-surface-variant leading-relaxed">
            <p>
              <strong>Solo-Recipes: Seasoning Stitcher</strong> is crafted around the concept of <strong>Modern Organicism</strong>. It bridges the gap between digital utility and tactile editorial cooking aesthetics.
            </p>
            
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
              <div className="bg-surface-container-lowest p-3 rounded-xl border border-outline-variant/10">
                <span className="block text-xs font-bold text-primary uppercase tracking-wider mb-1">Typography Pairing</span>
                <p className="text-xs leading-relaxed text-on-surface-variant/90">
                  Elegant <strong>Playfair Display</strong> headings reflect high-end cookbooks, while structured <strong>Inter</strong> handles functional ingredients arrays.
                </p>
              </div>
              <div className="bg-surface-container-lowest p-3 rounded-xl border border-outline-variant/10">
                <span className="block text-xs font-bold text-primary uppercase tracking-wider mb-1">Color Coordinates</span>
                <p className="text-xs leading-relaxed text-on-surface-variant/90">
                  A calming <strong>Forest Green</strong> palette evokes raw botanicals and herbs, resting on high-contrast <strong>Warm Earth</strong> and soft cream cards.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Maintenance / Danger Card */}
        <div className="bg-surface-container-lowest rounded-2xl p-gutter shadow-sm border border-outline-variant/10 space-y-4">
          <h3 className="font-sans text-sm font-bold text-error flex items-center gap-1.5 uppercase tracking-wider">
            <ShieldCheck className="w-4 h-4 text-error" /> Storage & Maintenance
          </h3>

          <div className="flex items-center justify-between py-2">
            <div className="space-y-0.5 max-w-md">
              <span className="block font-sans text-sm font-semibold text-on-surface">Factory Reset App State</span>
              <span className="block text-xs text-on-surface-variant">Clears local storage, restores the default 7 fresh pantry ingredients, and removes all recipe favorites</span>
            </div>
            
            <button
              onClick={handleResetClick}
              className="px-4 py-2 bg-error/10 text-error hover:bg-error/20 active:scale-95 text-xs font-bold rounded-full transition-colors flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5" /> Reset State
            </button>
          </div>
        </div>

        {/* Brand signature */}
        <div className="text-center pt-8 text-on-surface-variant/50 font-serif text-xs">
          Solo-Recipes © 2026 • Crafted for Organic Culinary Artistry
        </div>
      </main>
    </div>
  );
}
