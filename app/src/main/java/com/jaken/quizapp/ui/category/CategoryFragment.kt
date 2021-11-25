package com.jaken.quizapp.ui.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaken.quizapp.MainViewModel
import com.jaken.quizapp.databinding.FragmentCategoryBinding
import com.jaken.quizapp.ui.category.adapter.CategoryViewAdapter
import com.jaken.quizapp.ui.quiz.QuizFragment
import com.jaken.quizapp.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment: Fragment() {

    private val viewModel: CategoryViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    private lateinit var categoryViewAdapter: CategoryViewAdapter
    private lateinit var binding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding
            .inflate(inflater, container, false).apply {
                lifecycleOwner = viewLifecycleOwner
                viewmodel = viewModel
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCategoryRecyclerView()
        setObservers()
    }


    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            categoryViewAdapter.items = state.categories
            categoryViewAdapter.notifyDataSetChanged()
        })
        categoryViewAdapter.onClickItem.observe(viewLifecycleOwner, EventObserver { category ->
            mainViewModel.changeFragment(
                QuizFragment().apply {
                    val bundle = Bundle()
                    bundle.putInt(QuizFragment.CATEGORY_ID, category.id)
                    arguments = bundle
                }
            )
        })
    }

    private fun initCategoryRecyclerView() {
        activity?.let {
            categoryViewAdapter = CategoryViewAdapter()
            binding.recyclerViewCategories.apply {
                adapter = categoryViewAdapter
                layoutManager = LinearLayoutManager(it.applicationContext)
                itemAnimator = DefaultItemAnimator()
                setHasFixedSize(true)
            }
        }
    }
}