package com.musaguzel.anketgundem.firestore

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SomeFirebaseJobs:CoroutineScope, ViewModel() {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    //Anket tıklama oranlarını güncelleme
    fun updateField(documentID: String, fieldString: String, newValue: Double) {
        launch {
            firebaseFirestore = Firebase.firestore
            firebaseFirestore.collection("Posts").document(documentID)
                .update(fieldString, newValue)
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                    println(it.localizedMessage)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}