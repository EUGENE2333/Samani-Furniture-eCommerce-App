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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samani.R
import com.example.samani.adapters.BestDealsHomePageAdapter
import com.example.samani.adapters.BestProductsAdapter
import com.example.samani.adapters.SpecialProductsAdapter
import com.example.samani.data.MyListProduct
import com.example.samani.data.repository.SharedPreferencesRepository
import com.example.samani.databinding.FragmentMainCategoryBinding
import com.example.samani.util.Resource
import com.example.samani.util.showBottomNavigationView
import com.example.samani.viewmodel.MainCategoryViewModel
import com.example.samani.viewmodel.MyListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsHomePageAdapter
    private val mainCategoryViewModel by viewModels<MainCategoryViewModel>()
    private val myListViewModel by viewModels<MyListViewModel>()

    @Inject
    lateinit var sharedPreferencesRepository: SharedPreferencesRepository

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
        binding.tvSeeAllDeals.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_bestDealsFragment)
        }
        bestProductsAdapter.onImgFavouriteClick ={
            myListViewModel.addProductToWishlist(MyListProduct(product = it))

        }
        bestProductsAdapter.onImgFavouriteFilledClick ={
            myListViewModel.deleteWishlistProduct(MyListProduct(product = it))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCategoryViewModel.specialProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showLoading()
                        }
                        is Resource.Success -> {
                            specialProductsAdapter.differ.submitList(it.data)
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
                binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, _, _, _ ->
                    if (scrollX >= v.getChildAt(0).width - v.width) {
                        mainCategoryViewModel.fetchSpecialProducts()
                    }

                })
            }
        }



        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCategoryViewModel.bestDealsProducts.collectLatest {
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
                        }
                        else -> Unit

                    }
                }

                binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, _, _, _ ->
                    if (scrollX >= v.getChildAt(0).width - v.width) {
                        mainCategoryViewModel.fetchBestDealsProducts()
                    }

                })
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCategoryViewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            bestProductsAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            Log.e(TAG, it.message.toString())
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit

                    }

                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY) {
                mainCategoryViewModel.fetchBestProducts()
            }


        })
    }

    private fun setUpBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter(sharedPreferencesRepository)
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpBestDealsRv() {
        bestDealsAdapter = BestDealsHomePageAdapter()
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
