package com.a24rsolution.account

import com.google.firebase.database.ServerValue

data class Claims(val upiID:String, val claimAmount: String, val claimDate: MutableMap<String, String> = ServerValue.TIMESTAMP)
