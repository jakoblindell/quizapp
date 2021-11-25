package com.jaken.quizapp.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jaken.quizapp.databinding.FragmentQuizBinding
import com.jaken.quizapp.util.EventObserver
import com.jaken.quizapp.util.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment: Fragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
    }

    private val viewModel: QuizViewModel by viewModels()

    private lateinit var binding: FragmentQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding
            .inflate(inflater, container, false).apply {
                lifecycleOwner = viewLifecycleOwner
                viewmodel = viewModel
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryId = arguments?.getInt(CATEGORY_ID) ?: return
        viewModel.getQuiz(categoryId)
        setObservers()
    }

    private fun setObservers() {
        observe(viewModel.dialog, this, viewModel::pressedPositiveDialogAction, viewModel::pressedNegativeDialogAction)
        viewModel.finish.observe(viewLifecycleOwner, EventObserver {
            activity?.onBackPressed()
        })
    }

}