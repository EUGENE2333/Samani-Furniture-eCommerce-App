package com.example.samani.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.samani.R
import com.example.samani.adapters.BestDealsAdapter
import com.example.samani.databinding.FragmentBestDealsBinding
import com.example.samani.util.Resource
import com.example.samani.util.hideBottomNavigationView
import com.example.samani.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "BestProductsFragment"

@AndroidEntryPoint
class BestDealsFragment: Fragment() {
 private lateinit var binding: FragmentBestDealsBinding
    private lateinit var bestDealsAdapter: BestDealsAdapter
 private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBestDealsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBestDealsRv()

        bestDealsAdapter.onClick = {
            val bundle = Bundle().apply{ putParcelable("product",it)}
            findNavController().navigate(R.id.action_bestDealsFragment_to_productDetailsFragment,bundle)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestDealsProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            bestDealsAdapter.differ.submitList(it.data)
                            hideLoading()
                        }
                        is Resource.Error -> {
                            Log.e(TAG, it.message.toString())
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                            hideLoading()
                        }
                        else -> Unit

                    }

                }
            }
        }

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_bestDealsFragment_to_homeFragment)
        }
        hideBottomNavigationView()

    }

    private fun hideLoading() {
       binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDeals.apply{
            layoutManager = GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            adapter = bestDealsAdapter
        }
    }
}
