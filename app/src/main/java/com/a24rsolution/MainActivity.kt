package com.a24rsolution

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.a24rsolution.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = findNavController(R.id.myNavHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _: NavController, nd: NavDestination, _: Bundle? ->
            if (nd.id == R.id.homeFragment || nd.id == R.id.brandsFragment || nd.id == R.id.profileFragment){
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
            else{
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

    }
}