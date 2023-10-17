package com.example.samani.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.samani.R
import com.example.samani.adapters.BestDealsAdapter
import com.example.samani.adapters.BestProductsAdapter
import com.example.samani.adapters.SpecialProductsAdapter
import com.example.samani.databinding.FragmentMainCategoryBinding
import com.example.samani.util.Resource
import com.example.samani.util.showBottomNavigationView
import com.example.samani.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private const val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter

    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpecialProductsRv()
        setUpBestDealsRv()
        setUpBestProductsRv()

        specialProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

        bestDealsAdapter.onClick = {
            val bundle = Bundle().apply{ putParcelable("product",it)}
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,bundle)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                         Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }
                    else -> Unit

                }

            }
            binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,scrollX,_,_,_ ->
                if( scrollX >= v.getChildAt(0).width - v.width){
                    viewModel.fetchSpecialProducts()
                }

            })
        }



        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit

                }
            }

            binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,scrollX,_,_,_ ->
                if( scrollX >= v.getChildAt(0).width - v.width){
                    viewModel.fetchBestDealsProducts()
                }

            })
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit

                }

            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.fetchBestProducts()
            }


        })
    }

    private fun setUpBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestDealsAdapter
        }
    }



    private fun setUpSpecialProductsRv() {
       specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply{
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsAdapter
        }
    }


    private fun hideLoading() {
        binding.apply {
            mainCategoryProgressBar.visibility = View.GONE
            tvSeeAllDeals.visibility = View.VISIBLE
            backgroundDisplay.visibility = View.VISIBLE
            imgForward.visibility = View.VISIBLE
            viewLine.visibility = View.VISIBLE
            tvBestDeals.visibility = View.VISIBLE
            tvBestProducts.visibility = View.VISIBLE
        }
    }

    private fun showLoading() {
        binding.apply {
            mainCategoryProgressBar.visibility = View.VISIBLE
            tvSeeAllDeals.visibility = View.GONE
            backgroundDisplay.visibility = View.GONE
            imgForward.visibility = View.GONE
            viewLine.visibility = View.GONE
            tvBestDeals.visibility = View.GONE
            tvBestProducts.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}












