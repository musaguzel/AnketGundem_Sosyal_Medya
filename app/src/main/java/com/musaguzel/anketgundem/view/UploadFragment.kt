package com.musaguzel.anketgundem.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.snackbar.Snackbar
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.adapter.TagAdapter
import com.musaguzel.anketgundem.adapter.UploadTagAdapter
import com.musaguzel.anketgundem.util.ProgressButton
import com.musaguzel.anketgundem.util.ProgressButtonUpload
import com.musaguzel.anketgundem.viewmodel.UploadViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_interest_tags.*
import kotlinx.android.synthetic.main.fragment_upload.*


class UploadFragment : Fragment() {


    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var progressButton: ProgressButtonUpload
    private var selectedPostImage: Uri? = null
    private var postSelectedImage: Uri? = null
    private lateinit var uploadTagAdapter: UploadTagAdapter
    private lateinit var viewModel: UploadViewModel

    lateinit var avd: AnimatedVectorDrawableCompat
    lateinit var avd2: AnimatedVectorDrawable

    var error: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(UploadViewModel::class.java)

        recyclerviewUpload.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        uploadTagAdapter = UploadTagAdapter(addNames())
        recyclerviewUpload.adapter = uploadTagAdapter

        progressButton = ProgressButtonUpload(view.context,view)

        registerLauncher()
        context?.let { setPostImage(it,view) }
        uploadGoGalleryText.setOnClickListener {
            setPostImage(it.context,it)
        }

        observeLiveData()
        uploadButtonListener()

    }

    fun addNames() : ArrayList<String>{
        val tagnames = ArrayList<String>()
        tagnames.add("#Doğa")
        tagnames.add("#Hayvanlar")
        tagnames.add("#Yemek")
        tagnames.add("#Moda")
        tagnames.add("#Teknoloji")
        tagnames.add("#Spor")
        tagnames.add("#Fotoğraf")
        tagnames.add("#Diğer")
        return tagnames
    }

    fun uploadButtonListener(){
        buttonYayinla.setOnClickListener {
            val comment1 = multiTxtFirst.text.toString().trim()
            val comment2 = multiTxtSecond.text.toString().trim()
            val comment3 = multiTxtThird.text.toString().trim()
            val commentList = ArrayList<String>()
            commentList.add(comment1)
            commentList.add(comment2)
            commentList.add(comment2)
            if (selectedPostImage.toString().equals("")|| comment1.equals("") || comment2.equals("") || comment3.equals("") || UploadViewModel.tagPositions.positions.isEmpty()){
                Toast.makeText(context,"Bilgiler eksik",Toast.LENGTH_LONG).show()
                error = true
            }else{
                error = false
                viewModel.uploadPost(selectedPostImage,commentList)
            }
        }
    }

    fun observeLiveData(){
        viewModel.uploadLoading.observe(viewLifecycleOwner, Observer { loading ->
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

        viewModel.uploadSuccessful.observe(viewLifecycleOwner, Observer { uploadSuccessful ->
            uploadSuccessful?.let {
                if (uploadSuccessful){
                    uploadConstraintLayout.visibility = View.VISIBLE
                    uploadConstraintLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fadein))
                    val drawable: Drawable = doneUpload.drawable
                    if (drawable is AnimatedVectorDrawableCompat) {
                        avd = drawable
                        avd.start()
                    }else if (drawable is AnimatedVectorDrawable){
                        avd2 = drawable
                        avd2.start()
                    }

                    uploadLinearTop.visibility = View.INVISIBLE
                    recyclerviewUpload.visibility = View.INVISIBLE
                    uploadLinearText.visibility = View.GONE
                    uploadLine.visibility = View.GONE
                    buttonYayinla.visibility = View.GONE
                }
            }
        })
    }

    fun setPostImage(context: Context,view: View) {  //eğer izin yoksa izin iste varsa galeriye git
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this.requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(view, "Galeri için izin lazım", Snackbar.LENGTH_INDEFINITE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val rslt = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                postSelectedImage = rslt.uri
                uploadPostImage.setImageURI(postSelectedImage)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = rslt.error
                Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPostImage = intentFromResult.data
                        context?.let {
                            CropImage.activity(selectedPostImage)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setActivityTitle("Düzenle")
                                .setCropMenuCropButtonTitle("Bitti")
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