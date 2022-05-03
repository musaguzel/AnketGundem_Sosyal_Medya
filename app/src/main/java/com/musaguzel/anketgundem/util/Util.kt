package com.musaguzel.anketgundem.util

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.musaguzel.anketgundem.R
import java.io.File

fun ImageView.getImageFromFirebase(url: String?, shimmerDrawable: ShimmerDrawable) {

    val options = RequestOptions()
        .placeholder(shimmerDrawable)
        .error(R.mipmap.ic_launcher_round)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)

}

fun createDirectory(dir: File) {
    if (!dir.exists()){
        if (!dir.mkdirs()){
           dir.mkdirs()
        }
    }
}

fun placeholderShimmer(context: Context): ShimmerDrawable {

    val shimmer =
        Shimmer.ColorHighlightBuilder()
            .setBaseColor(Color.parseColor("#FFFFFF"))
            .setHighlightColor(Color.parseColor("#E8E8E8"))
            .setDuration(600)
            .setBaseAlpha(0.7F)
            .setHighlightAlpha(0.8f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

    return ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

}

/*fun placeholderProgressBar(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}*/


//adapter da positionlar ayarlanacak
//Upload ekranında edittextlere background verilebilir. Hazır görsel eklenebilir post fotosu için
//Yayınla Tıklanıldığında başka bir fragmentta yeşil renkte anketiniz yayınlandı gibi bir şey gösterilebilir. Ya da aynı fragmentta viewmodel ile observe edilip
//o yeşil şeyi observe ederek gösterilebilir.
//beğeni tıklandığında userid o postun beğeni arrayında eklenecek