package com.musaguzel.anketgundem.model

import android.widget.TextView
import com.google.firebase.firestore.DocumentId
import java.util.*
import kotlin.collections.ArrayList
data class Posts(
        @DocumentId
        val documentId: String = "",
        val CommentClick0: Double = 0.0,
        val CommentClick1: Double = 0.0,
        val CommentClick2: Double = 0.0,
        val CommentList: List<String> = ArrayList(),
        val date: Date = Date(),
        val imageUrl: String? = "",
        val likes: Int = 0,
        val pphotoUrl: String = "",
        val userName: String = "",
        var selectCommentIndex: Boolean? = null,
        var selectedTextViewIndex: Int? = null
)








