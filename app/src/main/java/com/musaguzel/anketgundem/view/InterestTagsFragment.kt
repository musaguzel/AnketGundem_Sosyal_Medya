package com.musaguzel.anketgundem.view

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.adapter.PostAdapter
import com.musaguzel.anketgundem.adapter.TagAdapter
import kotlinx.android.synthetic.main.fragment_interest_tags.*


class InterestTagsFragment : Fragment(){



    private lateinit var tagAdapter:TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interest_tags, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerviewTags.layoutManager = LinearLayoutManager(context)
        tagAdapter = TagAdapter(addNames())
        recyclerviewTags.adapter = tagAdapter

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






}