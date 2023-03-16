package com.a24rsolution.modProfile

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ModProfileViewModel: ViewModel() {
    private var rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: DatabaseReference = Firebase.database.reference
    private  val userId = mAuth.currentUser!!.uid


    var name = MutableLiveData<String>()


    var email = MutableLiveData<String>()


    var dob = MutableLiveData<String>()


    var mobile = MutableLiveData<String>()

    var profile = MutableLiveData<String>()

    private val _onSaveEvent = MutableLiveData<Boolean>()
    val onSaveEvent: LiveData<Boolean>
        get() = _onSaveEvent

    private val _nameValidate = MutableLiveData<Boolean>()
    val nameValidate: LiveData<Boolean>
        get() = _nameValidate

    private val _emailValidate = MutableLiveData<Boolean>()
    val emailValidate: LiveData<Boolean>
        get() = _emailValidate

    private val _mobileValidate = MutableLiveData<Boolean>()
    val mobileValidate: LiveData<Boolean>
        get() = _mobileValidate

    private val _dobValidate = MutableLiveData<Boolean>()
    val dobValidate: LiveData<Boolean>
        get() = _dobValidate




    companion object{
        const val TAG = "ProfileUpdateFragment"
    }
    init {
        _onSaveEvent.value = false
        getProfile()
    }

    private fun getProfile() {
        val profileListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.child("name").exists()) {
                    name.value = dataSnapshot.child("name").value.toString()
                    dob.value = dataSnapshot.child("dob").value.toString()
                    email.value = dataSnapshot.child("email").value.toString()
                    mobile.value = dataSnapshot.child("mobile").value.toString()
                    profile.value = dataSnapshot.child("profile").value.toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        rootRef.child("Users").child(userId).addValueEventListener(profileListener)
    }

    fun onSaveChanges(){
        if(validateFields())
        {
            database.child("Users").child(userId).child("profile").setValue(profile.value)
            database.child("Users").child(userId).child("name").setValue(name.value)
            database.child("Users").child(userId).child("email").setValue(email.value)
            database.child("Users").child(userId).child("mobile").setValue(mobile.value)
            database.child("Users").child(userId).child("dob").setValue(dob.value)
            _onSaveEvent.value = true
        }

    }

    private fun validateFields(): Boolean {
        _nameValidate.value = TextUtils.isEmpty(name.value)
        _emailValidate.value = TextUtils.isEmpty(email.value)
        _mobileValidate.value = TextUtils.isEmpty(mobile.value)
        _dobValidate.value = TextUtils.isEmpty(dob.value)

        return (!_nameValidate.value!! &&
                !_emailValidate.value!! &&
                !_mobileValidate.value!! &&
                !_dobValidate.value!!)
    }
}