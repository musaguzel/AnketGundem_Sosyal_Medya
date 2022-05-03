package com.musaguzel.anketgundem.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.musaguzel.anketgundem.model.Posts
import kotlinx.coroutines.launch

class AnaSayfaViewModel(application: Application) : BaseViewModel(application) {

    val firebaseFirestore = FirebaseFirestore.getInstance()

    var listState: Parcelable? = null

    val posts = MutableLiveData<List<Posts>>()
    val postError = MutableLiveData<Boolean>()
    val postLoading = MutableLiveData<Boolean>()


    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val sharedPreferences =
        context.getSharedPreferences("com.musaguzel.anketgundem", Context.MODE_PRIVATE)


    fun refreshData() {
        getPostData()
    }

    //Post Data çekme
    fun getPostData() {
        val savedSet = sharedPreferences.getStringSet("tagset", hashSetOf())
        val ttagList: ArrayList<String> = arrayListOf()
        if (savedSet != null) {
            TagList.taglist.clear()
            TagList.taglist.addAll(savedSet)
        }

        TagList.taglist.forEach {
            ttagList.add(it)
            println(it)
        }

        ttagList.add("doga")

        postLoading.value = true

        launch {
            firebaseFirestore.collection("Posts").whereArrayContainsAny("tags", ttagList)
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

                                if (selectedIndexList.isNotEmpty()) {
                                    //println("deneme" + selectedList.indices)
                                    for (i in postList.lastIndex downTo 0) {
                                        if (selectedIndexList.contains(postList[i].documentId)) {
                                            postList[i].selectCommentIndex = true
                                        }
                                    }
                                }
                                posts.value = postList
                                postLoading.value = false
                                postError.value = false
                            }
                        }
                    }
                }
        }

    }

    companion object SelectedPosition {
        var selectedIndexList = hashSetOf<String>()
        var selectedTextViewIndex = hashSetOf<String>()
        // var selectedIndexList : ArrayList<Int> = ArrayList()
    }

    object TagList {
        var taglist: HashSet<String> = hashSetOf()
        var tagPosition: HashSet<String> = hashSetOf()
    }

}



