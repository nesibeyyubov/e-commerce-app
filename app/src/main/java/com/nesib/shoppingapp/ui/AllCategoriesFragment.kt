package com.nesib.shoppingapp.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nesib.shoppingapp.R
import com.nesib.shoppingapp.adapters.CategoryAdapter
import kotlinx.android.synthetic.main.fragment_all_categories.*
import kotlinx.android.synthetic.main.fragment_home.*

class AllCategoriesFragment : Fragment(R.layout.fragment_all_categories) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        val categoryAdapter = CategoryAdapter(true)
        categoryAdapter.categoryClickListener = { category ->
            val action = AllCategoriesFragmentDirections.actionAllCategoriesFragmentToCategoryFragment()
            action.category = category
            findNavController().navigate(action)
        }
        allCategoriesRecyclerView.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(this@AllCategoriesFragment.context)
            setHasFixedSize(true)
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}