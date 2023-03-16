package com.a24rsolution.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.a24rsolution.R
import com.a24rsolution.databinding.FragmentLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_REQUEST_CODE = 1001
    }


    private lateinit var navController: NavController
    private lateinit var database: DatabaseReference

    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        navController = findNavController()
        database = Firebase.database.reference
        binding.loginButton.setOnClickListener {
            launchSignInFlow()
        }
        return binding.root
    }


    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        @Suppress("DEPRECATION")
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_app_logo) // Set logo drawable
                .setTheme(R.style.Theme_24rsolution) // Set theme
                .setTosAndPrivacyPolicyUrls(
                    "https://24rsolution.com/",
                    "https://24rsolution.com/"
                )
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                checkUserStatus()
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }


    private fun checkUserStatus() {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val myRef = FirebaseDatabase.getInstance().reference
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.child("name").exists()){
                    if (navController.currentDestination?.id == R.id.loginFragment) {
                        navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                    }
                }
                else{
                    if (navController.currentDestination?.id == R.id.loginFragment) {
                        navController.navigate(LoginFragmentDirections.actionLoginFragmentToSetUpFragment())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("SetUpViewModel", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.child("Users").child(userId).addValueEventListener(profileListener)
    }

}