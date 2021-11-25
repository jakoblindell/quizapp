package com.jaken.quizapp.data.remote.dto

import android.os.Build
import android.text.Html
import com.jaken.quizapp.domain.model.Quiz
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizResponseDto(
    @SerialName("response_code")
    val responseCode: Int,
    val results: List<QuizDto>,
)

@Serializable
data class QuizDto(
    @SerialName("correct_answer")
    val correctAnswer: String,
    @SerialName("incorrect_answers")
    val incorrectAnswers: List<String>,
    val category: String,
    val question: String,
    val difficulty: Difficulty,
    val type: String,
) {
    fun toQuiz(): Quiz {
        val formattedQuestion: String
        val formattedAnswers = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formattedQuestion = Html.fromHtml(question, Html.FROM_HTML_MODE_LEGACY).toString()
            formattedAnswers.addAll(
                incorrectAnswers.map { Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString() }
            )
            formattedAnswers.add(Html.fromHtml(correctAnswer, Html.FROM_HTML_MODE_LEGACY).toString())
        } else {
            formattedQuestion = Html.fromHtml(question).toString()
            formattedAnswers.addAll(
                incorrectAnswers.map { Html.fromHtml(it).toString() }
            )
            formattedAnswers.add(Html.fromHtml(correctAnswer).toString())
        }
        val answers = formattedAnswers.shuffled()
        val correctAnswerIndex = answers.indexOf(correctAnswer)
        return Quiz(
            formattedAnswers,
            correctAnswerIndex,
            correctAnswer,
            category,
            formattedQuestion,
            difficulty,
        )
    }
}

@Serializable
enum class Difficulty {
    @SerialName("easy")
    EASY,
    @SerialName("medium")
    MEDIUM,
    @SerialName("hard")
    HARD
}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}