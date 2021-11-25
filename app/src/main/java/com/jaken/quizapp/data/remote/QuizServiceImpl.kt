package com.jaken.quizapp.data.remote

import com.jaken.quizapp.data.remote.dto.CategoryResponseDto
import com.jaken.quizapp.data.remote.dto.QuizDto
import com.jaken.quizapp.data.remote.dto.QuizResponseDto
import com.jaken.quizapp.domain.model.Category
import com.jaken.quizapp.domain.model.Quiz
import io.ktor.client.*
import io.ktor.client.request.*

class QuizServiceImpl(
    private val client: HttpClient
): QuizService {

    override suspend fun getAllCategories(): List<Category> {
        return try {
            client.get<CategoryResponseDto>(QuizService.Endpoints.GetAllCategories.url)
                .triviaCategories
                .map { it.toCategory() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getQuizFor(categoryId: Int): List<Quiz> {
        return try {
            client.get<QuizResponseDto>(QuizService.Endpoints.GetQuizFor(categoryId).url)
                .results
                .map { it.toQuiz() }
        } catch (e: Exception) {
            emptyList()
        }
    }

}