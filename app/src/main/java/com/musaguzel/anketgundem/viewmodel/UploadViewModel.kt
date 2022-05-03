package com.musaguzel.anketgundem.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.musaguzel.anketgundem.model.Posts
import com.musaguzel.anketgundem.model.UserInfo
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class UploadViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseUser: FirebaseUser

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val uploadLoading = MutableLiveData<Boolean>()
    val uploadSuccessful = MutableLiveData<Boolean>()

    fun uploadPost(selectedPostImage: Uri? , commentList: ArrayList<String>){
        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth
        storage = Firebase.storage
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        uploadLoading.value = false

        if (selectedPostImage != null){
            uploadLoading.value = true
            launch {
                val reference = storage.reference
                val UUID = UUID.randomUUID()
                val imageName = "$UUID.jpg"
                val imageReference = reference.child("anketImages").child(imageName)

                imageReference.putFile(selectedPostImage).addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener {

                        firestore.collection("Users").document(firebaseUser.uid).addSnapshotListener { value, error ->
                            if (error != null){
                                Toast.makeText(context,error.localizedMessage,Toast.LENGTH_LONG).show()
                            }else{
                                if (value != null){
                                    val data = hashMapOf(
                                        "CommentClick0" to 0,
                                        "CommentClick1" to 0,
                                        "CommentClick2" to 0,
                                        "CommentList" to commentList,
                                        "date" to Timestamp(Date()),
                                        "imageUrl" to it.toString(),
                                        "likes" to 1,
                                        "pphotoUrl" to value.get("photoUrl"),
                                        "tags" to tagPositions.positions,
                                        "userName" to value.get("userName")
                                    )
                                    firestore.collection("Posts").add(data).addOnSuccessListener {
                                        Toast.makeText(context,"Post Başarıyla Yüklendi",Toast.LENGTH_LONG).show()
                                        uploadLoading.value = false
                                        uploadSuccessful.value = true
                                    }.addOnFailureListener {
                                        Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                                        uploadLoading.value = false
                                        uploadSuccessful.value = false
                                    }
                                }
                            }
                        }

                    }
                }.addOnFailureListener {
                    uploadLoading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }else {
            Toast.makeText(context,"Fotoğraf yok", Toast.LENGTH_LONG).show()

        }

    }

    object tagPositions{
        var positions: ArrayList<String> = arrayListOf()
    }
}