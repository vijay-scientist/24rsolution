package com.a24rsolution.initiate

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.a24rsolution.R
import com.a24rsolution.databinding.FragmentSetUpBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

private var myMoney: Int = 0

class SetUpFragment : Fragment() {
    private lateinit var binding: FragmentSetUpBinding
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private var profile: String? = null
    private var reward: Int = 0
    private var credit: Int = 0

    private val myCalendar: Calendar = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_up, container, false)
        navController = findNavController()
        receiveDynamicLink()
        getFinance()


        binding.setUpDobEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(), date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        binding.setUpProfilePickerShapeAbleImageView.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            @Suppress("DEPRECATION")
            startActivityForResult(pickPhoto, 1000)
        }

        binding.setUpSaveUserProfileButton.setOnClickListener {
            if (validateFields()) {
                if (binding.setUpReferCodeEditText.text.isNullOrEmpty()){
                    writeUser(
                        binding.setUpNameTextInputLayout.editText?.text.toString(),
                        binding.setUpEmailTextInputLayout.editText?.text.toString(),
                        binding.setUpMobileTextInputLayout.editText?.text.toString(),
                        binding.setUpDobTextInputLayout.editText?.text.toString(),
                        profile.toString(),
                        binding.setUpReferCodeTextInputLayout.editText?.text.toString(),
                        myMoney = myMoney.plus(reward).toString()
                    )
                }else {
                    dynamicRewards()
                    writeUser(
                        binding.setUpNameTextInputLayout.editText?.text.toString(),
                        binding.setUpEmailTextInputLayout.editText?.text.toString(),
                        binding.setUpMobileTextInputLayout.editText?.text.toString(),
                        binding.setUpDobTextInputLayout.editText?.text.toString(),
                        profile.toString(),
                        binding.setUpReferCodeTextInputLayout.editText?.text.toString(),
                        myMoney = myMoney.plus(credit).plus(reward).toString()
                    )
                }
            }

        }


        return binding.root
    }

    private fun dynamicRewards() {
        val referrerUid = binding.setUpReferCodeEditText.text.toString()
        val referrerRef = FirebaseDatabase.getInstance().reference
        val moneyListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var refMoney = snapshot.child("money").value.toString().toInt()
                    refMoney += credit
                    Firebase.database.reference.child("Users").child(referrerUid)
                        .child("money").setValue(refMoney.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SetUpFragment", error.message)
            }

        }
        referrerRef.child("Users").child(referrerUid)
            .addListenerForSingleValueEvent(moneyListener)

        val connectionListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val installedUsers = arrayListOf<String>()
                if (snapshot.exists()) {
                    for (dss in snapshot.child("connections").children) {
                        val usersList: String = dss.value.toString()
                        installedUsers.add(usersList)
                    }

                }
                installedUsers.add(FirebaseAuth.getInstance().currentUser!!.uid)
                Firebase.database.reference.child("Users").child(referrerUid)
                    .child("connections").setValue(installedUsers)


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SetUpFragment", error.message)
            }

        }
        referrerRef.child("Users").child(referrerUid)
            .addListenerForSingleValueEvent(connectionListener)

    }

    private fun getFinance() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val financeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    reward = snapshot.child("reward").value.toString().toInt()
                    credit = snapshot.child("credit").value.toString().toInt()
                    /*Log.i("SetUpFragment", "$reward  $credit")*/
                }
            }

            override fun onCancelled(error: DatabaseError) {
                /*Log.e("SetUpFragment", error.message)*/
            }

        }
        rootRef.child("Finance").addListenerForSingleValueEvent(financeListener)
    }

    private fun receiveDynamicLink() {
        Firebase.dynamicLinks
            .getDynamicLink(requireActivity().intent)
            .addOnSuccessListener(requireActivity()) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.i("SetUpFragment", deepLink.toString())
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...
                val user = FirebaseAuth.getInstance().currentUser
               /* Log.i("SetUpFragment", user.toString())*/
                Log.i("SetUpFragment", user.toString())
                Log.i("SetUpFragment", deepLink.toString())
                if (user != null &&
                    deepLink != null &&
                    deepLink.getBooleanQueryParameter("invitedby", false)
                ) {
                    val referrerUid = deepLink.getQueryParameter("invitedby")
                    Log.i("SetUpFragment", referrerUid.toString())
                    binding.setUpReferCodeTextInputLayout.editText?.setText(referrerUid.toString())

                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                Log.w(
                    "SetUpFragment",
                    "getDynamicLink:onFailure",
                    e
                )
            }
    }


    private fun writeUser(
        name: String,
        email: String,
        mobile: String,
        dob: String,
        profile: String,
        referrerUid: String,
        myMoney: String
    ) {
        val database = Firebase.database.reference
        val user = User(name, email, mobile, dob, profile, referrerUid, myMoney)
        database.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(user)
            .addOnSuccessListener {
                if (navController.currentDestination?.id == R.id.setUpFragment) {
                    navController.navigate(SetUpFragmentDirections.actionSetUpFragmentToHomeFragment())
                } else {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun validateFields(): Boolean {
        val name = TextUtils.isEmpty(binding.setUpNameTextInputLayout.editText?.text)
        val email = TextUtils.isEmpty(binding.setUpEmailTextInputLayout.editText?.text)
        val mobile = TextUtils.isEmpty(binding.setUpMobileTextInputLayout.editText?.text)
        val dob = TextUtils.isEmpty(binding.setUpDobTextInputLayout.editText?.text)
        val picCheck = TextUtils.isEmpty(profile)

        if (name) {
            binding.setUpNameTextInputLayout.error = "Field cannot be empty"
        } else {
            binding.setUpNameTextInputLayout.error = null
        }

        if (email) {
            binding.setUpEmailTextInputLayout.error = "Field cannot be empty"
        } else {
            binding.setUpEmailTextInputLayout.error = null
        }

        if (mobile) {
            binding.setUpMobileTextInputLayout.error = "Field cannot be empty"
        } else {
            binding.setUpMobileTextInputLayout.error = null
        }

        if (dob) {
            binding.setUpDobTextInputLayout.error = "Field cannot be empty"
        } else {
            binding.setUpDobTextInputLayout.error = null
        }
        if (picCheck) {
            Toast.makeText(requireContext(), "Upload a profile first...", Toast.LENGTH_SHORT).show()
        }


        return (!name &&
                !email &&
                !mobile &&
                !picCheck &&
                !dob)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK && data != null) {
                val selectedImage: Uri? = data.data
                uploadImageToFirebase(selectedImage)
            }
        }

    }

    private fun uploadImageToFirebase(selectedImage: Uri?) {
        startProgressCircular()
        storageReference = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        val profileRef =
            storageReference.child("usersProfile/${mAuth.currentUser!!.uid} /profile.jpg")
        val uploadTask = selectedImage?.let { profileRef.putFile(it) }

        uploadTask?.addOnSuccessListener {
            profileRef.downloadUrl.addOnSuccessListener {
                Glide
                    .with(requireContext())
                    .load(it)
                    .into(binding.setUpProfilePictureShapeAbleImageView)
                profile = it.toString()
                stopProgressCircular()

            }
        }

        uploadTask?.addOnFailureListener {
            stopProgressCircular()
            Toast.makeText(context, "Profile has not been uploaded", Toast.LENGTH_SHORT).show()
        }
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
        binding.setUpDobEditText.setText(sdf.format(myCalendar.time))
    }

    private fun startProgressCircular() {
        binding.setUpProgressCircular.visibility = View.VISIBLE
        binding.setUpProgressCircularText.visibility = View.VISIBLE
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        markButtonDisable()
    }

    private fun stopProgressCircular() {
        binding.setUpProgressCircular.visibility = View.GONE
        binding.setUpProgressCircularText.visibility = View.GONE
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        markButtonEnable()
    }

    private fun markButtonDisable() {
        binding.setUpSaveUserProfileButton.isEnabled = false
    }

    private fun markButtonEnable() {
        binding.setUpSaveUserProfileButton.isEnabled = true
    }


}

