package com.example.samani.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.samani.adapters.MyListAdapter
import com.example.samani.databinding.FragmentMyListBinding
import com.example.samani.viewmodel.MyListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyListFragment: Fragment() {
    private lateinit var binding: FragmentMyListBinding
    private val viewModel by viewModels<MyListViewModel>()
    private val myListAdapter by lazy { MyListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}