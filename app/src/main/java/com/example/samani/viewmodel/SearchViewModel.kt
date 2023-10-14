package com.example.samani.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samani.data.Product
import com.example.samani.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
): ViewModel() {
   private val  _searchedProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchedProducts = _searchedProducts.asStateFlow()

    var noResultsFound = MutableStateFlow(false)


    init {
        getProductsByName("")
    }

    fun getProductsByName(searchQuery: String) {
        if (searchQuery.isNotEmpty()) {
            viewModelScope.launch {
                _searchedProducts.emit(Resource.Loading())
            }

            firestore.collection("Products")
                .whereArrayContains(
                    "keywords",
                    searchQuery.toLowerCase(Locale.ROOT)
                )
                .limit(50)
                .get()
                .addOnSuccessListener {
                    val results = it.toObjects(Product::class.java)
                    if (results.isEmpty()) {
                        viewModelScope.launch {
                            noResultsFound.value = true
                        }
                    }
                    viewModelScope.launch {
                        _searchedProducts.emit(Resource.Success(results))
                        noResultsFound.value = false
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _searchedProducts.emit(Resource.Error(it.message.toString()))
                        noResultsFound.value = false
                    }
                }
        }
    }
}