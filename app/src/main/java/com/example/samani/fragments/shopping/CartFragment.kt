package com.example.samani.fragments.shopping
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samani.R
import com.example.samani.adapters.CartProductAdapter
import com.example.samani.data.CartProduct
import com.example.samani.databinding.FragmentCartBinding
import com.example.samani.firebase.FirebaseCommon
import com.example.samani.util.Resource
import com.example.samani.util.VerticalItemDecoration
import com.example.samani.util.showBottomNavigationView
import com.example.samani.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CartFragment:Fragment(R.layout.fragment_cart){
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy{CartProductAdapter()}
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartRv()
        var totalPrice = 0f

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest {price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "Ksh $price"
                }

            }
        }

        cartAdapter.onProductClick = {
            val bundle = Bundle().apply{putParcelable("product",it.product)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,bundle)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray(),true)
            findNavController().navigate(action)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel"){ dialog,_->
                        dialog.dismiss()

                    }
                    setPositiveButton("Yes"){ dialog, _->
                         viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()

            }

        }


        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                       binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                      if(it.data!!.isEmpty()){
                          binding.progressbarCart.visibility = View.INVISIBLE
                          showEmptyCart()
                          hideOtherViews()
                      }else{
                          hideEmptyCart()
                          showOtherViews()
                          cartAdapter.differ.submitList(it.data)
                          binding.progressbarCart.visibility = View.INVISIBLE
                      }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply{
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply{
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
      binding.apply{
          layoutCartEmpty.visibility = View.GONE
      }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpCartRv() {
        binding.rvCart.apply{
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

}