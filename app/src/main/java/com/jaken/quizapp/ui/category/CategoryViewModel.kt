package com.jaken.quizapp.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaken.quizapp.data.remote.QuizService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val quizService: QuizService
): ViewModel() {

    private val _state = MutableLiveData(CategoryState())
    val state: LiveData<CategoryState> = _state

    init {
        getAllCategories()
    }


    private fun getAllCategories() {
        viewModelScope.launch {
            _state.value = state.value?.copy(isLoading = true)
            val result = quizService.getAllCategories()
            _state.value = state.value?.copy(
                categories = result,
                isLoading = false
            )
        }
    }
}