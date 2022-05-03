package com.musaguzel.anketgundem.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.viewmodel.UploadViewModel
import kotlinx.android.synthetic.main.updatetagrecycler.view.*

class UploadTagAdapter(val tagNames: ArrayList<String>) : RecyclerView.Adapter<UploadTagAdapter.TagsViewHolder>() {
    class TagsViewHolder(var view: View) : RecyclerView.ViewHolder(view)  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadTagAdapter.TagsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.updatetagrecycler, parent, false)
        return UploadTagAdapter.TagsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UploadTagAdapter.TagsViewHolder, position: Int) {

            holder.view.uploadrecyclertxt.text = tagNames[position]


        if (UploadViewModel.tagPositions.positions.contains(tagNames[position])){
            holder.view.uploadrecyclertxt.setBackgroundColor(Color.GREEN)

        }else{
            holder.view.uploadrecyclertxt.setBackgroundColor(Color.parseColor("#E6E6E6"))
        }

        holder.view.uploadrecyclertxt.setOnClickListener {
            if (UploadViewModel.tagPositions.positions.contains(tagNames[position])){
                UploadViewModel.tagPositions.positions.remove(tagNames[position])
                it.setBackgroundColor(Color.parseColor("#E6E6E6"))
            }else{
                UploadViewModel.tagPositions.positions.add(tagNames[position])
                it.setBackgroundColor(Color.GREEN)
            }
        }
    }

    override fun getItemCount(): Int {
       return tagNames.size
    }
}