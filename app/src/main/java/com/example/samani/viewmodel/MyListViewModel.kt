package com.example.samani.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samani.data.MyListProduct
import com.example.samani.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _myListProducts =MutableStateFlow<Resource<List<MyListProduct>>>(Resource.Unspecified())
    val myListProducts = _myListProducts.asStateFlow()

    private val _isProductAdded = MutableStateFlow(false)
    val isProductAdded = _isProductAdded.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<MyListProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()


    private var wishlistProductDocuments = emptyList<DocumentSnapshot>()

    init {
        getWishlistProducts()
    }

    private fun getWishlistProducts(){
        viewModelScope.launch {
            _myListProducts.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .addSnapshotListener { value, error ->
                if(error != null || value == null){
                    viewModelScope.launch { _myListProducts.emit(Resource.Error(error?.message.toString())) }
                }else{
                    wishlistProductDocuments =value.documents
                    val cartProducts = value.toObjects(MyListProduct::class.java)
                    viewModelScope.launch {_myListProducts.emit(Resource.Success(cartProducts))}
                }
            }
    }

    fun deleteWishlistProduct(myListProduct: MyListProduct){
        val index = _myListProducts.value.data?.indexOf(myListProduct)
        if(index != null) {
            val documentId =wishlistProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("wishlist")
                .document(documentId).delete()
        }


    }

}