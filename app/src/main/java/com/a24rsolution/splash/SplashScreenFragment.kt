package com.a24rsolution.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.a24rsolution.R
import com.a24rsolution.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SplashScreenFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSplashScreenBinding
    private val viewModel by viewModels<SplashScreenViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash_screen, container, false)
        navController = findNavController()
        val anim = RotateAnimation(0F, 720F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.repeatCount = Animation.ABSOLUTE
        anim.duration = 3000
        binding.splashScreenLogo.startAnimation(anim)
        Handler(Looper.myLooper()!!).postDelayed({
            observeAuthenticationState()
        }, 5000) // 3000 is the delayed time in milliseconds.
        return binding.root
    }


    private fun observeAuthenticationState() {

        viewModel.authenticationState.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                SplashScreenViewModel.AuthenticationState.AUTHENTICATED -> checkUserStatus()
                else -> sendUserToLoginFragment()

            }
        })
    }

    private fun sendUserToLoginFragment() {
        if (navController.currentDestination?.id == R.id.splashScreenFragment) {
            navController.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment())
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
                    if (navController.currentDestination?.id == R.id.splashScreenFragment) {
                        navController.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToHomeFragment())
                    }
                }
                else{
                    if (navController.currentDestination?.id == R.id.splashScreenFragment) {
                        navController.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToSetUpFragment())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("SetUpViewModel", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.child("Users").child(userId).addListenerForSingleValueEvent(profileListener)
    }
}