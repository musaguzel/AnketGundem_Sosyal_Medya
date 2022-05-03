package com.musaguzel.anketgundem.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.model.Posts
import com.musaguzel.anketgundem.util.getImageFromFirebase
import com.musaguzel.anketgundem.util.placeholderShimmer
import kotlinx.android.synthetic.main.recycler_user_posts.view.*

class UserPostAdapter(val userPosts: ArrayList<Posts>): RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder>() {

    class UserPostViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostAdapter.UserPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_user_posts, parent, false)
        return UserPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPostAdapter.UserPostViewHolder, position: Int) {

            holder.view.userPost1.getImageFromFirebase(userPosts[position].imageUrl, placeholderShimmer(holder.view.context))
            holder.view.userPost2.getImageFromFirebase(userPosts[position+1].imageUrl, placeholderShimmer(holder.view.context))
            holder.view.userPost3.getImageFromFirebase(userPosts[position+2].imageUrl, placeholderShimmer(holder.view.context))

    }

    override fun getItemCount(): Int {
       return userPosts.size / 2
    }

    fun updateUserPostList(newPostList: ArrayList<Posts>) {
        userPosts.clear()
        userPosts.addAll(newPostList)
        notifyDataSetChanged()
    }
}