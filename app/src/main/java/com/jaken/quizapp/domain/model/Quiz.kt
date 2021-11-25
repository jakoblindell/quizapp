package com.jaken.quizapp.domain.model

import com.jaken.quizapp.data.remote.dto.Difficulty

data class Quiz(
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val correctAnswer: String,
    val category: String,
    val question: String,
    val difficulty: Difficulty,
)

enum class Answer {
    A, B, C, D
}