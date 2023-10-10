package com.example.samani.fragments.categories


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.samani.data.Category
import com.example.samani.util.Resource
import com.example.samani.viewmodel.BaseCategoryViewModel
import com.example.samani.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CupboardFragment: BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<BaseCategoryViewModel>{
        BaseCategoryViewModelFactory(firestore,Category.Cupboard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showOfferLoading()

                    }
                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideOfferLoading()

                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showBestProductsLoading()

                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideBestProductsLoading()

                    }
                    else -> Unit
                }
            }
        }

    }

    //TODO: Paging Impl

    override fun onBestProductsPagingRequest() {
        super.onBestProductsPagingRequest()
        viewModel.fetchBestProducts()
    }

    override fun onOfferPagingRequest() {
        super.onOfferPagingRequest()
        viewModel.fetchOfferProducts()
    }

}