package com.a24rsolution.home

import android.content.Intent
import android.net.Uri
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
import com.a24rsolution.databinding.FragmentHomeBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        navController = findNavController()
        getMoney()

        binding.homeMyAccount.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToAccountFragment())
        }
        binding.homeReferAndEarn.setOnClickListener{
            createDynamicLink()
        }

        binding.homeLogout.setOnClickListener{
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    sendUserToLoginFragment()
                }
        }
        return binding.root
    }

    private fun getMoney() {
        val rootRef= FirebaseDatabase.getInstance().reference
        val moneyListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.homeMyMoneyTextView.text = snapshot.child("money").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", error.message)
            }

        }
        rootRef.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(moneyListener)
    }

    private fun createDynamicLink() {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://www.24rsolution.com/")
            domainUriPrefix = "https://24rsolutionapp.page.link"
            // Open links with this app on Android
            androidParameters {
                minimumVersion = 23
            }
        }

        val dynamicLinkUri = dynamicLink.uri

        Log.i("HomeFragment", dynamicLink.toString())


        //shorten the dynamic link
        val user = FirebaseAuth.getInstance().currentUser!!
        val uid = user.uid
        val invitationLink = "$dynamicLinkUri?invitedby=$uid"
        Log.i("HomeFragment", invitationLink)
        Firebase.dynamicLinks.shortLinkAsync {
            longLink = Uri.parse(invitationLink)
        }.addOnSuccessListener { (shortLink, _) ->
            // You'll need to import com.google.firebase.dynamiclinks.ktx.component1 and
            // com.google.firebase.dynamiclinks.ktx.component2
            Log.i("HomeFragment", shortLink.toString())
            // Short link created
            processShortLink(shortLink)
        }.addOnFailureListener {
            // Error
            // ...
        }
    }

    private fun processShortLink(shortLink: Uri?) {

        val inviteIntent = Intent(Intent.ACTION_SEND)
        inviteIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
        inviteIntent.type = "text/plain"
        startActivity(inviteIntent)

    }

    private fun sendUserToLoginFragment() {
        navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
    }

}