package com.jaken.quizapp.data.remote.dto

import com.jaken.quizapp.domain.model.Category
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class CategoryResponseDto(
    @SerialName("trivia_categories")
    val triviaCategories: List<CategoryDto>,
)

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String
) {
    fun toCategory() = Category(id, name)
}