package com.musaguzel.anketgundem.util

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.musaguzel.anketgundem.R
import kotlinx.android.synthetic.main.progress_btn_layout.view.*

class ProgressButton {
    private var cardView: CardView
    private var constraintLayout: ConstraintLayout
    private var progressBar: ProgressBar
    private var textView: TextView
    private var fade_in: Animation

    constructor(context: Context, view: View) {

        fade_in = AnimationUtils.loadAnimation(context,R.anim.fadein)
        cardView = view.findViewById(R.id.cardviewButton)
        constraintLayout = view.findViewById(R.id.constrain_layout_button)
        progressBar = view.findViewById(R.id.progressBarForButton)
        textView = view.findViewById(R.id.textViewForButton)
    }

    fun buttonActivated() {
        progressBar.animation = fade_in
        progressBar.visibility = View.VISIBLE
        textView.animation = fade_in
        textView.setText("Bilgileriniz Güncelleniyor...")
    }

    fun buttonFinished() {
        constraintLayout.setBackgroundColor(cardView.resources.getColor(R.color.greenForButton))
        progressBar.visibility = View.GONE
        textView.setText("Tamamlandı")
    }

    fun buttonError() {
        constraintLayout.setBackgroundColor(cardView.resources.getColor(R.color.purple_200))
        progressBar.visibility = View.GONE
        textView.setText("Lütfen Bilgileri Eksiksiz Girin")
    }

}