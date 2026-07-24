package com.example.solo_recipes.ui.stitcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solo_recipes.data.FlavorDataSource
import com.example.solo_recipes.model.FoodCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlavorStitcherViewModel(private val dataSource: FlavorDataSource) : ViewModel() {

    private val _categories = MutableStateFlow<List<FoodCategory>>(emptyList())
    val categories: StateFlow<List<FoodCategory>> = _categories

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)
    val selectedCategory: StateFlow<FoodCategory?> = _selectedCategory

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = dataSource.getFlavorCategories()
        }
    }

    fun selectCategory(category: FoodCategory?) {
        _selectedCategory.value = category
    }
}
