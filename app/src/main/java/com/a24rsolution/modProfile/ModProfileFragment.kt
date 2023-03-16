package com.a24rsolution.modProfile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.a24rsolution.R
import com.a24rsolution.databinding.FragmentModProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class ModProfileFragment : Fragment() {
    private lateinit var storageReference: StorageReference
    private lateinit var mAuth: FirebaseAuth
    private val myCalendar: Calendar = Calendar.getInstance()
    private val viewModel by viewModels<ModProfileViewModel>()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentModProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mod_profile, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        navController = findNavController()
        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference


        binding.modProfileDobEditText.setOnClickListener{
            DatePickerDialog(
                requireContext(), date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
        binding.modProfilePickerShapeAbleImageView.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            @Suppress("DEPRECATION")
            startActivityForResult(pickPhoto, 10)
        }

        viewModel.onSaveEvent.observe(viewLifecycleOwner, { saved->
            if (saved)
                sendUserToProfileFragment()
        })

        viewModel.nameValidate.observe(viewLifecycleOwner, {isEmpty->
            if (isEmpty){
                binding.modProfileNameTextInputLayout.error = "Field cannot be empty"
            }
            else{
                binding.modProfileNameTextInputLayout.error = null
            }
        })
        viewModel.emailValidate.observe(viewLifecycleOwner, {isEmpty->
            if (isEmpty){
                binding.modProfileEmailTextInputLayout.error = "Field cannot be empty"
            }
            else{
                binding.modProfileEmailTextInputLayout.error = null
            }
        })
        viewModel.mobileValidate.observe(viewLifecycleOwner, {isEmpty->
            if (isEmpty){
                binding.modProfileMobileTextInputLayout.error = "Field cannot be empty"
            }
            else{
                binding.modProfileMobileTextInputLayout.error = null
            }
        })
        viewModel.dobValidate.observe(viewLifecycleOwner, {isEmpty->
            if (isEmpty){
                binding.modProfileDobTextInputLayout.error = "Field cannot be empty"
            }
            else{
                binding.modProfileDobTextInputLayout.error = null
            }
        })


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val selectedImage: Uri? = data.data
                uploadImageToFirebase(selectedImage)
            }
        }

    }

    private fun uploadImageToFirebase(selectedImage: Uri?) {
        startProgressCircular()
        val profileRef = storageReference.child("usersProfile/${mAuth.currentUser!!.uid} /profile.jpg")
        val uploadTask = selectedImage?.let { profileRef.putFile(it) }

        uploadTask?.addOnSuccessListener {
            Toast.makeText(context, "Profile has been uploaded", Toast.LENGTH_SHORT).show()
            profileRef.downloadUrl.addOnSuccessListener {
                viewModel.profile.value = it.toString()
                stopProgressCircular()

            }
        }

        uploadTask?.addOnFailureListener{
            Toast.makeText(context, "Profile has not been uploaded", Toast.LENGTH_SHORT).show()
            stopProgressCircular()
        }
    }

    private fun sendUserToProfileFragment() {
        navController.navigate(ModProfileFragmentDirections.actionModProfileFragmentToProfileFragment())
    }

    private var date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar[Calendar.YEAR] = year
        myCalendar[Calendar.MONTH] = monthOfYear
        myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        updateLabel()
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.modProfileDobEditText.setText(sdf.format(myCalendar.time))
    }

    private fun startProgressCircular(){
        binding.modProfileProgressCircular.visibility = View.VISIBLE
        binding.modProfileProgressCircularText.visibility = View.VISIBLE
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        markButtonDisable()
    }

    private fun stopProgressCircular(){
        binding.modProfileProgressCircular.visibility = View.GONE
        binding.modProfileProgressCircularText.visibility = View.GONE
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        markButtonEnable()
    }

    private fun markButtonDisable() {
        binding.modProfileSaveUserProfileButton.isEnabled = false
    }

    private fun markButtonEnable() {
        binding.modProfileSaveUserProfileButton.isEnabled = true
    }
}
