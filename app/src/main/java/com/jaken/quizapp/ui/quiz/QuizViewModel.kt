package com.jaken.quizapp.ui.quiz

import android.content.res.Resources
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaken.quizapp.R
import com.jaken.quizapp.data.remote.QuizService
import com.jaken.quizapp.domain.model.Answer
import com.jaken.quizapp.domain.model.Quiz
import com.jaken.quizapp.util.Event
import com.jaken.quizapp.util.SimpleDialog
import com.jaken.quizapp.util.SimpleDialogModel
import com.jaken.quizapp.util.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


sealed class QuizDialog: SimpleDialog {
    object ReadyToStart : QuizDialog()
    object QuestionAnsweredCorrect : QuizDialog()
    data class QuestionAnsweredWrong(val correctAnswer: String) : QuizDialog()
    data class QuestionTimerRanOut(val correctAnswer: String) : QuizDialog()
    data class QuizFinished(val correctAnswers: Int, val totalQuestions: Int, val averageTimeSpent: Int?): QuizDialog()

    override fun model(resources: Resources): SimpleDialogModel {
        return when (this) {
            is ReadyToStart -> SimpleDialogModel(
                R.string.ready_to_start.map(resources),
                confirmAction = R.string.start.map(resources),
                cancelAction = R.string.cancel.map(resources)
            )
            is QuestionAnsweredCorrect -> SimpleDialogModel(
                R.string.question_answer_correct.map(resources),
                confirmAction = R.string.next_question.map(resources)
            )
            is QuestionAnsweredWrong -> SimpleDialogModel(
                R.string.question_answer_wrong.map(resources),
                confirmAction = R.string.next_question.map(resources)
            )
            is QuestionTimerRanOut -> SimpleDialogModel(
                R.string.question_timer_ran_out.map(resources),
                confirmAction = R.string.next_question.map(resources)
            )
            is QuizFinished -> SimpleDialogModel(
                R.string.quiz_finished_title.map(resources),
                R.string.quiz_finished_body.map(resources, correctAnswers, totalQuestions, averageTimeSpent),
                confirmAction = R.string.ok.map(resources)
            )
        }
    }
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizService: QuizService
): ViewModel() {

    private val _state = MutableLiveData(QuizState())
    val state: LiveData<QuizState> = _state
    private val _finish = MutableLiveData<Event<Unit>>()
    val finish: LiveData<Event<Unit>> = _finish

    val dialog = MutableLiveData<SimpleDialog?>()

    private val quizTimer = QuizTimer(this)

    override fun onCleared() {
        super.onCleared()
        quizTimer.cancel()
    }

    fun getQuiz(categoryId: Int) {
        dialog.postValue(QuizDialog.ReadyToStart)
        viewModelScope.launch {
            _state.value = state.value?.copy(isLoading = true)
            val result = quizService.getQuizFor(categoryId)
            _state.value = state.value?.copy(
                isLoading = false,
                questions = result,
            )
        }
    }

    fun answerButtonPressed(answer: Answer) {
        quizTimer.cancel()
        when {
            state.value?.countdownTimerValue ?: 0 <= 0 -> {
                dialog.postValue(QuizDialog.QuestionTimerRanOut(
                    state.value?.activeQuestion?.correctAnswer ?: "")
                )
                _state.value = state.value?.copy(
                    countdownRunning = false,
                )
            }
            state.value?.activeQuestion?.correctAnswerIndex == answer.ordinal -> {
                dialog.postValue(QuizDialog.QuestionAnsweredCorrect)
                _state.value = state.value?.copy(
                    score = (state.value?.score ?: 0) + 1,
                    questionsLeft = (state.value?.questionsLeft ?: QuizService.QUESTIONS_AMOUNT) - 1,
                    countdownRunning = false,
                )
            }
            else -> {
                dialog.postValue(QuizDialog.QuestionAnsweredWrong(
                    state.value?.activeQuestion?.correctAnswer ?: "")
                )
                _state.value = state.value?.copy(
                    questionsLeft = (state.value?.questionsLeft ?: QuizService.QUESTIONS_AMOUNT) - 1,
                    countdownRunning = false,
                )
            }
        }
    }

    fun fiftyFiftyButtonPressed() {
        val activeQuestion = state.value?.activeQuestion ?: return
        val correctAnswer = activeQuestion.correctAnswer
        val wrongAnswers = activeQuestion.answers.filter { it != correctAnswer }
        val newAnswers = listOf(
            wrongAnswers[Random.nextInt(wrongAnswers.count())],
            correctAnswer,
        )
        _state.value = state.value?.copy(
            activeQuestion = Quiz(
                newAnswers,
                newAnswers.indexOf(correctAnswer),
                correctAnswer,
                activeQuestion.category,
                activeQuestion.question,
                activeQuestion.difficulty,
            ),
            fiftyFiftyLifelineAvailable = false,
        )
    }

    fun addTimeButtonPressed() {
        val currentTimerValue = state.value?.countdownTimerValue ?: 0
        _state.value = state.value?.copy(
            countdownTimerValue = (currentTimerValue) + 10,
            countdownTimerLifelineAvailable = false
        )
        quizTimer.refreshTimer()
    }

    fun pressedPositiveDialogAction(dialog: SimpleDialog) {
        when (dialog) {
            is QuizDialog.ReadyToStart,
            is QuizDialog.QuestionAnsweredCorrect,
            is QuizDialog.QuestionAnsweredWrong,
            is QuizDialog.QuestionTimerRanOut -> nextQuestion()
            is QuizDialog.QuizFinished -> _finish.postValue(Event(Unit))
        }
    }

    fun pressedNegativeDialogAction(dialog: SimpleDialog) {
        when (dialog) {
            is QuizDialog.ReadyToStart -> _finish.postValue(Event(Unit))
        }
    }

    fun onTimerTick() {
        val currentTimerValue = state.value?.countdownTimerValue ?: 0
        if (currentTimerValue > 0)
            _state.value = state.value?.copy(countdownTimerValue = (currentTimerValue) - 1)
    }

    fun onTimerFinish() {
        dialog.postValue(QuizDialog.QuestionTimerRanOut(state.value?.activeQuestion?.correctAnswer ?: ""))
        _state.value = state.value?.copy(
            countdownRunning = false,
        )
    }

    private fun nextQuestion() {
        val questions = state.value?.questions ?: return
        if (questions.isEmpty()) {
            val score = state.value?.score ?: 0
            dialog.postValue(
                QuizDialog.QuizFinished(
                    score,
                    QuizService.QUESTIONS_AMOUNT,
                    state.value?.timeSpent?.average()?.toInt(),
                )
            )
        } else {
            state.value?.countdownTimerValue?.let { state.value?.timeSpent?.add(16 - it) }
            _state.value = state.value?.copy(
                activeQuestion = questions.last(),
                questions = questions.dropLast(1),
                countdownTimerValue = 15,
                countdownRunning = true,
            )
            quizTimer.start()
        }
    }
}

class QuizTimer(private val viewModel: QuizViewModel) {
    var initialTick = true
    var countdown = (viewModel.state.value?.countdownTimerValue ?: 15) + 1

    private var timer = getNewTimer(countdown)

    private fun getNewTimer(countdownTimer: Int): CountDownTimer {
        return object: CountDownTimer(countdownTimer.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (initialTick) {
                    initialTick = false
                    return
                }
                viewModel.onTimerTick()
            }

            override fun onFinish() {
                viewModel.onTimerFinish()
                initialTick = true
            }
        }
    }

    fun refreshTimer() {
        timer.cancel()
        countdown = (viewModel.state.value?.countdownTimerValue ?: 15)
        timer = getNewTimer(countdown)
        timer.start()
    }

    fun start() {
        countdown = (viewModel.state.value?.countdownTimerValue ?: 15) + 1
        timer = getNewTimer(countdown)
        timer.start()
    }

    fun cancel() {
        timer.cancel()
        countdown = (viewModel.state.value?.countdownTimerValue ?: 15) + 1
        initialTick = true
    }
}