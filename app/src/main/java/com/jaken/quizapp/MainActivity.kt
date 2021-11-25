package com.jaken.quizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.jaken.quizapp.ui.category.CategoryFragment
import com.jaken.quizapp.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeFragment(CategoryFragment())
        setObservers()
    }

    private fun setObservers() {
        viewModel.changeFragment.observe(this, EventObserver { fragment ->
            changeFragment(fragment)
        })
    }


    private fun changeFragment(fragment: Fragment) {
        val fragmentCheck = supportFragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
        if (fragmentCheck != null && fragmentCheck.isVisible) {
            return
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerMain, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }


}