package com.a24rsolution.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileViewModel: ViewModel() {
    private var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private  val userId = mAuth.currentUser!!.uid


    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _dob = MutableLiveData<String>()
    val dob: LiveData<String>
        get() = _dob


    private val _mobile = MutableLiveData<String>()
    val mobile: LiveData<String>
        get() = _mobile

    private val _profile = MutableLiveData<String>()
    val profile: LiveData<String>
        get() = _profile


    companion object{
        const val TAG = "ProfileViewModel"
    }
    init {
        getProfile()
    }

    private fun getProfile() {
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.child("name").exists()) {
                    _name.value = dataSnapshot.child("name").value.toString()
                    _dob.value = dataSnapshot.child("dob").value.toString()
                    _email.value = dataSnapshot.child("email").value.toString()
                    _mobile.value = dataSnapshot.child("mobile").value.toString()
                    _profile.value = dataSnapshot.child("profile").value.toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        rootRef.child("Users").child(userId).addValueEventListener(profileListener)
    }
}