package com.a24rsolution.brands

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.a24rsolution.R
import com.a24rsolution.databinding.FragmentBrandsBinding
import com.a24rsolution.profile.ProfileViewModel
import com.google.firebase.database.*

class BrandsFragment : Fragment() {
    private lateinit var binding: FragmentBrandsBinding
    private lateinit var brandsArrayList: ArrayList<Brand>
    private val brandsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Brands")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_brands, container, false)
        binding.brandsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.brandsRecyclerView.setHasFixedSize(true)

        brandsArrayList = arrayListOf()

        getBrands()

        return binding.root
    }

    private fun getBrands() {
        val brandsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot.exists()) {
                    for(brandsSnapshot in dataSnapshot.children){
                        val brand = brandsSnapshot.getValue(Brand::class.java)
                        brandsArrayList.add(brand!!)
                    }
                    binding.brandsRecyclerView.adapter = BrandsAdapter(brandsArrayList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ProfileViewModel.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        brandsRef.addValueEventListener(brandsListener)
    }
}