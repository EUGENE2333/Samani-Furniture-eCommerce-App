package com.example.samani.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samani.data.MyListProduct
import com.example.samani.data.repository.SharedPreferencesRepository
import com.example.samani.firebase.FirebaseCommon
import com.example.samani.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _myListProducts =
        MutableStateFlow<Resource<List<MyListProduct>>>(Resource.Unspecified())
    val myListProducts = _myListProducts.asStateFlow()

    private val _myListProduct = MutableStateFlow<Resource<MyListProduct>>(Resource.Unspecified())
    val myListProduct = _myListProduct.asStateFlow()



    private var wishlistProductDocuments = emptyList<DocumentSnapshot>()

    init {
        getWishlistProducts()
    }

    private fun getWishlistProducts() {
        viewModelScope.launch {
            _myListProducts.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _myListProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    wishlistProductDocuments = value.documents
                    val cartProducts = value.toObjects(MyListProduct::class.java)
                    viewModelScope.launch { _myListProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }


    fun addProductToWishlist(myListProduct: MyListProduct) {
        viewModelScope.launch { _myListProduct.emit(Resource.Loading()) }

        val productId = myListProduct.product.id

        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .whereEqualTo("product.id", myListProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    addProduct(myListProduct)
                }
                sharedPreferencesRepository.putBoolean(productId, true)
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _myListProduct.emit(Resource.Error(it.message.toString()))
                }
                sharedPreferencesRepository.putBoolean(productId, false)
            }
    }


    private fun addProduct(myListProduct: MyListProduct) {
        firebaseCommon.addProductToWishlist(myListProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _myListProduct.emit(Resource.Success(myListProduct))
                else
                    _myListProduct.emit(Resource.Error(e.message.toString()))

            }

        }
    }

    fun deleteWishlistProduct(myListProduct: MyListProduct) {
        val productId = myListProduct.product.id
        val index = _myListProducts.value.data?.indexOf(myListProduct)

        if (index != null) {
            val documentId = wishlistProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("wishlist")
                .document(documentId).delete()

            sharedPreferencesRepository.putBoolean(productId, false)

        }

    }

}
