package com.example.samani.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samani.R
import com.example.samani.adapters.MyListAdapter
import com.example.samani.databinding.FragmentMyListBinding
import com.example.samani.util.Resource
import com.example.samani.util.VerticalItemDecoration
import com.example.samani.viewmodel.MyListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        setUpMyListRv()

        myListAdapter.onImageClick = {
            val bundle = Bundle().apply{putParcelable("product",it.product)}
            findNavController().navigate(R.id.action_myListFragment_to_productDetailsFragment,bundle)

        }
        myListAdapter.onRemoveClick ={
              viewModel.deleteWishlistProduct(it)
        }
        myListAdapter.onViewClick = {
            val bundle = Bundle().apply{putParcelable("product",it.product)}
            findNavController().navigate(R.id.action_myListFragment_to_productDetailsFragment,bundle)

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.myListProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (it.data!!.isEmpty()) {
                                binding.progressBar.visibility = View.INVISIBLE
                                showEmptyList()
                            } else {
                                hideEmptyList()
                                myListAdapter.differ.submitList(it.data)
                                binding.progressBar.visibility = View.INVISIBLE
                            }

                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit

                    }

                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteDialog.collectLatest {
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete item from cart")
                        setMessage("Do you want to delete this item from your cart?")
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()

                        }
                        setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteWishlistProduct(it)
                            dialog.dismiss()
                        }
                    }
                    alertDialog.create()
                    alertDialog.show()

                }

            }
        }

    }

    private fun hideEmptyList() {
       binding.tvEmptyList.visibility = View.GONE
    }

    private fun showEmptyList() {
        binding.tvEmptyList.visibility = View.VISIBLE
    }

    private fun setUpMyListRv() {
        binding.rvMyList.apply{
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = myListAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

}
