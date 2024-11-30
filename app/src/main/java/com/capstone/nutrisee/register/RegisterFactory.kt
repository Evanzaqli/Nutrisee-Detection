package com.capstone.nutrisee.login.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutrisee.register.RegisterViewModel

class RegisterFactory private constructor() : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: RegisterFactory? = null

        fun getInstance(context: Context): RegisterFactory =
            instance ?: synchronized(this) {
                instance ?: RegisterFactory()
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
