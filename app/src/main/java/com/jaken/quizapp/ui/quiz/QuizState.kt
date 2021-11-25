package com.jaken.quizapp.ui.quiz

import com.jaken.quizapp.data.remote.QuizService
import com.jaken.quizapp.domain.model.Quiz

data class QuizState(
    val questions: List<Quiz> = emptyList(),
    val isLoading: Boolean = false,
    val questionsLeft: Int = QuizService.QUESTIONS_AMOUNT,
    val activeQuestion: Quiz? = null,
    val score: Int = 0,
    val timeSpent: MutableList<Int> = mutableListOf(),
    val countdownTimerValue: Int = 15,
    val countdownRunning: Boolean = true,
    val countdownTimerLifelineAvailable: Boolean = true,
    val fiftyFiftyLifelineAvailable: Boolean = true,
)