package com.example.samani.data.repository

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
){
    fun getBoolean(key: String,defaultValue:Boolean) =
        sharedPreferences.getBoolean(key,defaultValue)

    fun putBoolean(key: String, value: Boolean){
        sharedPreferences.edit().putBoolean(key,value).apply()
    }

}