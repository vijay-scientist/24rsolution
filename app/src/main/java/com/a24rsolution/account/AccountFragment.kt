package com.a24rsolution.account

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.a24rsolution.R
import com.a24rsolution.databinding.FragmentAccountBinding
import com.a24rsolution.initiate.SetUpFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)
        binding.accountTransactionsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.accountTransactionsRecyclerView.setHasFixedSize(true)
        navController = findNavController()
        getMoney()
        binding.accountClaimButton.setOnClickListener {
            if (binding.accountMyMoneyTextView.text.toString().toInt() < 200){
                Toast.makeText(requireContext(), "You should have at least 200₹ to claim", Toast.LENGTH_SHORT).show()
            }
            else{
                if (validateField()){
                    newClaim()
                }
            }
        }




        return binding.root
    }

    private fun getMoney() {
        val rootRef= FirebaseDatabase.getInstance().reference
        val moneyListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.accountMyMoneyTextView.text = snapshot.child("money").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", error.message)
            }

        }
        rootRef.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(moneyListener)
    }

    private fun validateField(): Boolean {
        return if (TextUtils.isEmpty(binding.accountUpiIDTextInputLayout.editText?.text)){
            binding.accountUpiIDTextInputLayout.error = "Please Enter UPI ID to Accept Payment"
            false
        } else{
            binding.accountUpiIDTextInputLayout.error = null
            true
        }
    }

    private fun newClaim() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val claimsListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    Toast.makeText(requireContext(), "Your Previous Claim has not been resolved ", Toast.LENGTH_SHORT).show()
                }
                else{
                    writeClaim(binding.accountUpiIDTextInputLayout.editText?.text.toString(), binding.accountMyMoneyTextView.text.toString())
                    setMoney()
                    getMoney()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AccountFragment",error.message)
            }

        }
        rootRef.child("Claims").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(claimsListener)
    }

    private fun setMoney() {
        val database = Firebase.database.reference
        database.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("money").setValue("0")
    }


    private fun writeClaim(
        upiID: String,
        claimAmount: String
    ) {
        val database = Firebase.database.reference
        val claim = Claims(upiID, claimAmount )
        database.child("Claims").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(claim)
            .addOnSuccessListener {

            }
    }


}