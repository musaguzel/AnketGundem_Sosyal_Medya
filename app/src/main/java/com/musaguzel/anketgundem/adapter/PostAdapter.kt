package com.musaguzel.anketgundem.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.firestore.SomeFirebaseJobs
import com.musaguzel.anketgundem.model.Posts
import com.musaguzel.anketgundem.util.TimeAgo
import com.musaguzel.anketgundem.util.getImageFromFirebase
import com.musaguzel.anketgundem.util.placeholderShimmer
import com.musaguzel.anketgundem.viewmodel.AnaSayfaViewModel
import kotlinx.android.synthetic.main.recycler_posts.view.*
import kotlin.collections.ArrayList

class PostAdapter(val postList: ArrayList<Posts>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }


    //recycler xml ile adaptörü bağlama işlemi burada yapılıyor
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_posts, parent, false)
        return PostViewHolder(view)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {



        val post: Posts = postList[position]
        var fieldstring: String
        val selectedIndex = AnaSayfaViewModel.SelectedPosition
        val selectedList = selectedIndex.selectedIndexList
        val sharedPreferences = holder.view.context.applicationContext.getSharedPreferences(
            "com.musaguzel.anketgundem",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        holder.setIsRecyclable(false)

        val commentList = ArrayList<TextView>()
        commentList.add(holder.view.firstCommentText)
        commentList.add(holder.view.secondCommentText)                        //Textviewleri listeye koyma
        commentList.add(holder.view.thirdCommentText)

        val commentPercentList = ArrayList<TextView>()
        commentPercentList.add(holder.view.firstCommentPercent)
        commentPercentList.add(holder.view.secondCommentPercent)                        //PercentTextviewleri listeye koyma
        commentPercentList.add(holder.view.thirdCommentPercent)

        val commentClickList = ArrayList<Double>()
        commentClickList.add(post.CommentClick0)
        commentClickList.add(post.CommentClick1)                              //Tıklanma sayılarını listeye alma
        commentClickList.add(post.CommentClick2)

        for (i in commentList.indices) {
            commentList[i].text = post.CommentList[i]                        //yorumları veritabanıyla eşitleme
        }

        for (n in postList.indices) {
            if (selectedList.contains(postList[n].documentId)) {               //tıklamaları kontrol etme
                postList[n].selectCommentIndex = true
                for (s in commentList.indices) {
                    if (selectedIndex.selectedTextViewIndex.contains(postList[n].documentId + s)) {
                        postList[n].selectedTextViewIndex = s
                    }
                }
            }
        }
        val docUUID: String = postList[position].documentId                   //Döküman id alma
        val timeAgo = TimeAgo.getTimeAgo(post.date.time)
        holder.view.txtTarih.text = timeAgo
        holder.view.txtBegeniler.text = post.likes.toString() + " kişi bu anketi beğendi"
        holder.view.txtUserName.text = post.userName
        holder.view.pphoto.getImageFromFirebase(
            post.pphotoUrl,
            placeholderShimmer(holder.view.context)
        )
        //Resim bağlama
        holder.view.recyclerImageView.getImageFromFirebase(
            postList[position].imageUrl,
            placeholderShimmer(holder.view.context)
        )

        if (post.selectCommentIndex == true) {                                //tıklanmış tüm yorumları
            holder.view.seek_bar1.progressDrawable = ContextCompat.getDrawable(holder.view.context,R.drawable.progress_track)
            holder.view.seek_bar2.progressDrawable = ContextCompat.getDrawable(holder.view.context,R.drawable.progress_track)
            holder.view.seek_bar3.progressDrawable = ContextCompat.getDrawable(holder.view.context,R.drawable.progress_track)
            for (all in commentList.indices) {
                commentList[all].isEnabled = false                            //tıklanamaz yapma
                if (post.selectedTextViewIndex == all) {
                    commentPercentList[all].setTextColor(Color.parseColor("#09FF00"))    //Tıklanan yorumun rengini değiştirme
                    showPercent(holder)                                                            //Yüzdelik Txt leri visible yapma
                    calculatePercent(                                                              //Yüzdelikleri hesaplama
                        post.CommentClick0,
                        post.CommentClick1,
                        post.CommentClick2,
                        holder
                    )
                }
            }
        }

        for (i in commentList.indices) {
            commentList[i].setOnClickListener {
                selectedIndex.selectedIndexList.add(docUUID)
                selectedIndex.selectedTextViewIndex.add(docUUID + i)
                if (selectedList.isNotEmpty()) {
                    editor.putStringSet("uuidset", selectedList).apply()
                    editor.putStringSet("uuidandtextid", selectedIndex.selectedTextViewIndex)
                        .apply()
                    editor.commit()
                }
                //veritabanına veri gönderme
                fieldstring =
                    "CommentClick" + i        //tıklanma sayısını güncellemek için field ismini alma
                val newCount = commentClickList[i] + 1 //yeni yorum sayısını belirleme

                val someFirebaseJobs = SomeFirebaseJobs()
                someFirebaseJobs.updateField(
                    docUUID,
                    fieldstring,
                    newCount
                )

            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return position  //return super.getItemViewType(position)
    }


    //Kaç satır olacağını istiyor
    override fun getItemCount(): Int {
        return postList.size
    }

    fun showPercent(holder: PostViewHolder) {
        holder.view.firstCommentPercent.visibility = View.VISIBLE
        holder.view.secondCommentPercent.visibility = View.VISIBLE
        holder.view.thirdCommentPercent.visibility = View.VISIBLE

    }



    fun updatePostlist(newPostList: ArrayList<Posts>) {
        postList.clear()
        postList.addAll(newPostList)
        notifyDataSetChanged()
    }

    fun calculatePercent(
        count1: Double,
        count2: Double,
        count3: Double,
        holder: PostViewHolder
    ) {
        //tıklama sayılarını toplama
        val total: Double = count1 + count2 + count3
        // yüzdelerini alma
        val percent1: Double = (count1 / total) * 100
        val percent2: Double = (count2 / total) * 100
        val percent3: Double = (count3 / total) * 100

        //textleri ve progressleri ayarlama
        holder.view.firstCommentPercent.text = String.format("%.0f%%", percent1)
        holder.view.seek_bar1.progress = percent1.toInt()


        holder.view.secondCommentPercent.text = String.format("%.0f%%", percent2)
        holder.view.seek_bar2.progress = percent2.toInt()


        holder.view.thirdCommentPercent.text = String.format("%.0f%%", percent3)
        holder.view.seek_bar3.progress = percent3.toInt()
    }


}