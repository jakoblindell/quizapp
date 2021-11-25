package com.jaken.quizapp.data.remote

import com.jaken.quizapp.domain.model.Category
import com.jaken.quizapp.domain.model.Quiz

interface QuizService {
    suspend fun getAllCategories(): List<Category>
    suspend fun getQuizFor(categoryId: Int): List<Quiz>

    companion object {
        const val BASE_URL = "https://opentdb.com"
        const val QUESTIONS_AMOUNT = 10 // number of questions
        const val TYPE = "multiple" // answers
    }

    sealed class Endpoints(val url: String) {
        object GetAllCategories: Endpoints("$BASE_URL/api_category.php")
        data class GetQuizFor(val categoryId: Int): Endpoints(
            "$BASE_URL/api.php?type=$TYPE&amount=$QUESTIONS_AMOUNT&category=$categoryId"
        )
    }
}