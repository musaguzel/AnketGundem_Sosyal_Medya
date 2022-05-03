package com.musaguzel.anketgundem.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.musaguzel.anketgundem.model.UserInfo
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class EditProfileViewModel(application: Application) : BaseViewModel(application) {

    val userInfoForEdit = MutableLiveData<UserInfo>()
    val downloadUri = MutableLiveData<String>()
    val uploadLoading = MutableLiveData<Boolean>()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseUser: FirebaseUser
    private val context = getApplication<Application>().applicationContext

    fun refreshUserInfo(){
        getUserProfileInfo()
    }


//kullanıcı bilgilerini çeker
    fun getUserProfileInfo()  {
        auth = Firebase.auth
        firestore = Firebase.firestore

        auth.currentUser?.let {
            firestore.collection("Users").document(it.uid).addSnapshotListener { value, error ->
                if (error != null){
                    Toast.makeText(context,error.localizedMessage,Toast.LENGTH_LONG).show()
                }else{
                    if (value != null){
                        userInfoForEdit.value = value.toObject(UserInfo::class.java)
                    }
                }
            }
        }
    }
        //güncelle butonuna basıldığında seçilen bir foto varsa alır depoya yükler downloadurl alır.Foto yoksa default fotoyu çeker downloadurl' ye koyar
    fun listenUploadUserPhoto(selectedPhoto: Uri?){
        firestore = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        uploadLoading.value = false

        if (selectedPhoto != null){
            uploadLoading.value = true
            launch {
                val reference = storage.reference
                val imageReference = reference.child("user_profile_photo_images").child(firebaseUser.uid + ".jpg")

                imageReference.putFile(selectedPhoto).addOnSuccessListener {
                    uploadLoading.value = false
                    imageReference.downloadUrl.addOnSuccessListener {
                        downloadUri.value = it.toString()
                    }
                }.addOnFailureListener {
                    uploadLoading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }else {
            uploadLoading.value = true
            firestore.collection("Users").document(firebaseUser.uid).get().addOnCompleteListener {
                if (it.isSuccessful){
                    val document: DocumentSnapshot? = it.result
                    if (document != null){
                        downloadUri.value = document.getString("photoUrl")
                        uploadLoading.value = false
                    }
                }
            }

        }
    }
    //Kullanıcı bilgilerini günceller
    fun updateUserInfo(email: String,name:String,photoUrl:String,username:String){
        firestore = Firebase.firestore
        auth = Firebase.auth
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val userInfo = UserInfo(email,name,photoUrl,username)
        uploadLoading.value = false

        if (email != auth.currentUser?.email){
            uploadLoading.value = true
            firebaseUser.updateEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    uploadLoading.value = false
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context,e.localizedMessage,Toast.LENGTH_LONG).show()
                uploadLoading.value = false
            }
        }else{
            auth.currentUser?.let {
                uploadLoading.value = true
                firestore.collection("Users").document(it.uid).set(userInfo).addOnSuccessListener {
                    uploadLoading.value = false
                }.addOnFailureListener { ex ->
                    uploadLoading.value = false
                    Toast.makeText(context,ex.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}