package com.jaken.quizapp

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jaken.quizapp.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _changeFragment = MutableLiveData<Event<Fragment>>()
    val changeFragment: LiveData<Event<Fragment>> = _changeFragment

    fun changeFragment(fragment: Fragment) {
        _changeFragment.postValue(Event(fragment))
    }
}