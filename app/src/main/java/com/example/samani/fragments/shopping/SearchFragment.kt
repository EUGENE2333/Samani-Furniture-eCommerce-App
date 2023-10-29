package com.example.samani.fragments.shopping
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samani.R
import com.example.samani.adapters.SearchedItemAdapter
import com.example.samani.databinding.FragmentSearchBinding
import com.example.samani.util.Resource
import com.example.samani.util.hideBottomNavigationView
import com.example.samani.util.showBottomNavigationView
import com.example.samani.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchedItemAdapter: SearchedItemAdapter
    val viewModel by viewModels<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSearchedItemRv()
         // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }

        binding.searchEdittext.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.noResultsFound.value = false
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var searchFor = ""
                val searchText = s.toString().trim()
                if (searchText == searchFor)
                    return
                searchFor = searchText

                GlobalScope.launch(IO) {
                    delay(300)
                    if(searchText != searchFor)
                        return@launch
                    binding.searchEdittext.text.toString()
                    viewModel.getProductsByName(searchText)

                }

            }

            override fun afterTextChanged(p0: Editable?) {

                if(TextUtils.isEmpty(p0)){
                    binding.searchSuggestionRecyclerview.visibility = View.GONE
                } else{
                    binding.searchSuggestionRecyclerview.visibility = View.VISIBLE
                }

            }

        })

        searchedItemAdapter.onClick = {
                val bundle = Bundle().apply { putParcelable("product",it) }
                findNavController().navigate(R.id.action_searchFragment_to_productDetailsFragment,bundle)

        }

        lifecycleScope.launchWhenStarted {
            viewModel.noResultsFound.collectLatest {
                 if(it){
                     Snackbar.make(requireView(),"No search Result found",Snackbar.LENGTH_SHORT).show()
                 }
              }
            }

        lifecycleScope.launchWhenStarted {
            viewModel.searchedProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                        showBottomNavigationView()
                    }
                    is Resource.Success -> {
                        searchedItemAdapter.differ.submitList(it.data)
                        hideLoading()
                        hideBottomNavigationView()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideLoading()
                        showBottomNavigationView()

                    }
                    else -> Unit
                }
            }
        }

    }
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun setUpSearchedItemRv() {
        searchedItemAdapter = SearchedItemAdapter()
        binding.searchSuggestionRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = searchedItemAdapter

        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

}