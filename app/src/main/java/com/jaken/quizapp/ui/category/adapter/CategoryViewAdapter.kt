package com.jaken.quizapp.ui.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.jaken.quizapp.databinding.CategoryItemBinding
import com.jaken.quizapp.domain.model.Category
import com.jaken.quizapp.util.Event

class CategoryViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<Category> = emptyList()

    private val _onClickItem: MutableLiveData<Event<Category>> = MutableLiveData()
    val onClickItem: LiveData<Event<Category>> = _onClickItem

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val auctionsHolder = holder as? CategoryViewHolder ?: return
        auctionsHolder.bindView(items[position], _onClickItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryItemBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount() = items.size

    inner class CategoryViewHolder(private val binding: CategoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: Category, onItemClick: MutableLiveData<Event<Category>>) {
            binding.apply {
                textViewCategoryName.text = item.name
                root.setOnClickListener { onItemClick.postValue(Event(item)) }
            }
        }
    }
}