package com.musaguzel.anketgundem.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.musaguzel.anketgundem.model.Posts
import com.musaguzel.anketgundem.model.UserInfo
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class ProfileViewModel(application: Application) : BaseViewModel(application) {

    val userInfo = MutableLiveData<UserInfo>()
    val userPosts = MutableLiveData<List<Posts>>()
    val postError = MutableLiveData<Boolean>()

    val firestore = Firebase.firestore
    val auth = Firebase.auth
    private val context = getApplication<Application>().applicationContext

    fun refreshUserInfo() {
        getUserProfileInfoFromFirebase()
    }


    fun getUserPosts(username: String) {
        firestore.collection("Posts").whereEqualTo("userName", username)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    //Doldurulacak
                    println(exception.localizedMessage)
                    postError.value = true
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val postList: List<Posts> = snapshot.toObjects(Posts::class.java)
                            userPosts.value = postList
                        }
                    }
                }
            }
    }


    fun getUserProfileInfoFromFirebase() {
        launch {
            auth.currentUser?.let {
                val mutableLiveData = MutableLiveData<List<UserInfo>>()
                firestore.collection("Users").document(it.uid).addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                    } else {
                        if (value != null) {
                            userInfo.value = value.toObject(UserInfo::class.java)
                            val userName = value.get("userName").toString()
                            getUserPosts(userName)
                        }
                    }
                }
            }
        }
    }
}