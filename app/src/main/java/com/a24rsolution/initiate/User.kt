package com.a24rsolution.initiate

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ServerValue

@IgnoreExtraProperties
data class User(val name: String? = null, val email: String? = null, val mobile: String? = null, val dob:String? = null, val profile:String?, val referrerUid:String?= null, val money: String? = null, val createdAt: MutableMap<String, String> = ServerValue.TIMESTAMP) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
