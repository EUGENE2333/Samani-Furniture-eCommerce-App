package com.example.samani.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.samani.data.repository.SharedPreferencesRepository
import com.example.samani.firebase.FirebaseCommon
import com.example.samani.util.Constants.INTRODUCTION_SHARED_PREFERENCES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFireBaseAuth() = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseFiretoreDatabase()= Firebase.firestore

    @Provides
    fun providesIntroductionSharedPreferences(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SHARED_PREFERENCES,MODE_PRIVATE)

  @Provides
  @Singleton
    fun providesSharedPreferencesRepository(
        sharedPreferences: SharedPreferences
    ): SharedPreferencesRepository{
        return SharedPreferencesRepository(sharedPreferences)
    }


    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore,firebaseAuth)

}