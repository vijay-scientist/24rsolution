package com.a24rsolution.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.a24rsolution.FirebaseUserLiveData


class SplashScreenViewModel: ViewModel() {
    enum class AuthenticationState{
        AUTHENTICATED, UNAUTHENTICATED
    }
    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}