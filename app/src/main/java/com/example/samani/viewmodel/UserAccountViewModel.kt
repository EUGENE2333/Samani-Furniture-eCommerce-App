package com.example.samani.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.samani.MainApplication
import com.example.samani.data.User
import com.example.samani.util.RegisterValidation
import com.example.samani.util.Resource
import com.example.samani.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    app: Application
): AndroidViewModel(app) {

    private val productsStorage = FirebaseStorage.getInstance().reference
    
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init{
        getUser()
    }

    fun getUser(){
        viewModelScope.launch { _user.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch { _user.emit(Resource.Success(it)) }
                }

            }
            .addOnFailureListener {
                viewModelScope.launch{
                    _user.emit(Resource.Error(it.message.toString()))
                }

            }
    }

    private fun saveUserInformation(user: User,shouldRetrieveOldImage: Boolean) {
      firestore.runTransaction {transaction ->
          val documentRef = firestore.collection("user").document(auth.uid!!)
          if(shouldRetrieveOldImage){
              val currentUser = transaction.get(documentRef).toObject(User::class.java)
              val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
              transaction.set(documentRef,newUser)
          }else{
              transaction.set(documentRef,user)
          }
      }.addOnSuccessListener {
          viewModelScope.launch {
              _updateInfo.emit(Resource.Success(user))
          }
      }.addOnFailureListener {
          viewModelScope.launch{
              _user.emit(Resource.Error(it.message.toString()))
          }
      }
    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
         viewModelScope.launch {
             try{
                 val imageBitmap = MediaStore.Images.Media.getBitmap(
                     getApplication<MainApplication>().contentResolver,
                     imageUri
                 )
                 val byteArrayOutputStream = ByteArrayOutputStream()
                 imageBitmap.compress(Bitmap.CompressFormat.JPEG,96,byteArrayOutputStream)
                 val imageByteArray = byteArrayOutputStream.toByteArray()
                 val imageDirectory = productsStorage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                 val result = imageDirectory.putBytes(imageByteArray).await()
                 val imageUrl = result.storage.downloadUrl.await().toString()

                 saveUserInformation(user.copy(imagePath = imageUrl),false)


             }catch (e:Exception){
                 viewModelScope.launch{
                     _user.emit(Resource.Error(e.message.toString()))
                 }
             }
         }
    }

    fun updateUser(user:User, imageUri:Uri?){

        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if(!areInputsValid){
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your Inputs"))
            }
            return
        }
        viewModelScope.launch { _updateInfo.emit(Resource.Loading()) }

        if (imageUri == null){
            saveUserInformation(user,true)
        }else{
            saveUserInformationWithNewImage(user,imageUri)
        }


    }


}









