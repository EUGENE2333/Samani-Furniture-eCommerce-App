package com.example.samani.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samani.data.MyListProduct
import com.example.samani.firebase.FirebaseCommon
import com.example.samani.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToWishlistViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {


    private val _myListProduct = MutableStateFlow<Resource<MyListProduct>>(Resource.Unspecified())
    val myListProducts = _myListProduct.asStateFlow()

    private val _isProductAdded = MutableStateFlow(false)
    val isProductAdded = _isProductAdded.asStateFlow()

    fun addProductToWishlist(myListProduct: MyListProduct){
        viewModelScope.launch { _myListProduct.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .whereEqualTo("product.id",myListProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let{
                    if(it.isEmpty()){  //Add Product
                        addProduct(myListProduct)
                        viewModelScope.launch {
                            _isProductAdded.emit(true)
                        }

                    }else{
                        val product = it.first().toObject(MyListProduct::class.java)
                        if(product == myListProduct){
                            deleteWishlistProduct(myListProduct)
                            viewModelScope.launch {
                                _isProductAdded.emit(false)
                            }

                        }else{
                            addProduct(myListProduct)
                            viewModelScope.launch {
                                _isProductAdded.emit(true)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch{_myListProduct.emit(Resource.Error(it.message.toString()))}
            }

    }

    private fun addProduct(myListProduct: MyListProduct){
        firebaseCommon.addProductToWishlist(myListProduct){addedProduct, e ->
            viewModelScope.launch {
                if(e==null)
                    _myListProduct.emit(Resource.Success(myListProduct))
                else
                    _myListProduct.emit(Resource.Error(e.message.toString()))

            }

        }
    }

    fun deleteWishlistProduct(myListProduct: MyListProduct) {
        val documentId = myListProduct.product.id
        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .document(documentId).delete()

        viewModelScope.launch {
            _isProductAdded.emit(false)
        }
    }
}
