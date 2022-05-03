package com.musaguzel.anketgundem.view

import android.Manifest
import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.util.ProgressButton
import com.musaguzel.anketgundem.util.getImageFromFirebase
import com.musaguzel.anketgundem.util.placeholderShimmer
import com.musaguzel.anketgundem.viewmodel.EditProfileViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.progress_btn_layout.*
import java.util.*


class EditProfileFragment : Fragment() {

    private lateinit var viewmodel: EditProfileViewModel
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var progressButton: ProgressButton
    private val mHandler = Handler()
    var error: Boolean? = null

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPhoto: Uri? = null
    var circleSelectedPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
        viewmodel.refreshUserInfo()
        progressButton = ProgressButton(view.context,view)

        sharedPreferences = context?.applicationContext?.getSharedPreferences(
            "com.musaguzel.anketgundem",
            Context.MODE_PRIVATE
        )


        auth = Firebase.auth

        registerLauncher()
        setProfileImage()

        updateUserInfoFromViewModel()
        observeLiveData()
    }

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun updateUserInfoFromViewModel(){
        buttonUpdateUserInfo.setOnClickListener {
            viewmodel.listenUploadUserPhoto(circleSelectedPhoto)
            viewmodel.downloadUri.observe(viewLifecycleOwner, Observer { downloadUri ->
                downloadUri.let {
                    val name = editProfileName.text.toString().trim()
                    val username = editProfileUserName.text.toString().trim().replace(" ","").toLowerCase(Locale.ROOT)
                    val email = editProfileEmail.text.toString().trim().toLowerCase(Locale.ROOT)
                    if (name.equals("") || username.equals("") || !email.isValidEmail()){
                        error = true
                    }else{
                        error = false
                        viewmodel.updateUserInfo(email,name,it,username)
                    }

                }
            })
        }
    }
    fun observeLiveData(){
        viewmodel.userInfoForEdit.observe(viewLifecycleOwner, Observer { userInfo ->
            userInfo.let {
                if (it != null){
                    editProfileName.setText(it.nameSurname)
                    editProfileUserName.setText(it.userName)
                    editProfileEmail.setText(it.email) // düzenlenecek
                    if (it.photoUrl != ""){
                        editProfileImage.getImageFromFirebase(it.photoUrl, placeholderShimmer(requireContext()))
                    }
                }
            }
        })

        viewmodel.uploadLoading.observe(viewLifecycleOwner, Observer { loading ->
           loading?.let {
               if (loading == false && error == true){
                   progressButton.buttonError()
               }else if (loading){
                   progressButton.buttonActivated()
                   /*mHandler.postDelayed(object : Runnable{
                       override fun run() {
                           val action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
                           findNavController().navigate(action)
                       }
                   },2000)*/
               }else{
                   progressButton.buttonFinished()
               }
           }
        })
    }

    fun setProfileImage() {
        editProfileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    it.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this.requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(it, "Galeri için izin lazım", Snackbar.LENGTH_INDEFINITE)
                        .setAction(
                            "İzin Ver"
                        ) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val rslt = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                circleSelectedPhoto = rslt.uri
                editProfileImage.setImageURI(circleSelectedPhoto)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = rslt.error
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPhoto = intentFromResult.data
                        context?.let {
                            CropImage.activity(selectedPhoto)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setActivityTitle("Crop Image")
                                .setCropMenuCropButtonTitle("Bitti")
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .start(it, this)
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Toast.makeText(context, "İzin Gerekli", Toast.LENGTH_SHORT).show()
                }
            }
    }
}