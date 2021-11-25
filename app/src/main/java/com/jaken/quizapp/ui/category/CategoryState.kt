package com.jaken.quizapp.ui.category

import com.jaken.quizapp.domain.model.Category

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)