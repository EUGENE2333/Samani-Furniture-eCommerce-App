package com.example.samani.fragments.shopping

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samani.R
import com.example.samani.adapters.ColorsAdapter
import com.example.samani.adapters.ViewPager2Images
import com.example.samani.data.CartProduct
import com.example.samani.databinding.FragmentProductDetailsBinding
import com.example.samani.helper.getProductPrice
import com.example.samani.util.Resource
import com.example.samani.util.hideBottomNavigationView
import com.example.samani.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy{ ViewPager2Images() }
    private val colorsAdapter by lazy{ ColorsAdapter() }
    private val viewModel by viewModels<DetailsViewModel>()
    private var selectedColor: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupViewPager()
        setupColorsRv()

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor))
        }

        binding.buttonAddToCartFAB.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor))
        }




        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error ->{
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }



        binding.apply {
            tvProductName.text = product.name
            val priceAfterPercentage = product.offerPercentage.getProductPrice(product.price)
            tvProductPrice.text =  "Ksh ${String.format("%.2f", priceAfterPercentage)}"
            tvProductDescription.text = product.description
            tvProductSize.text = "Size: ${product.sizes}"

            if(product.sizes == null)
                tvProductSize.visibility = View.GONE

            if(product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.GONE

        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let{colorsAdapter.differ.submitList(it)}
    }

    private fun setupViewPager() {
        binding.apply{
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply{
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

}