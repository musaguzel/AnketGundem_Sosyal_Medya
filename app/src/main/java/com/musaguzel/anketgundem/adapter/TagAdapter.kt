package com.musaguzel.anketgundem.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.viewmodel.AnaSayfaViewModel
import kotlinx.android.synthetic.main.recycler_tags.view.*

class TagAdapter(val tagNames: ArrayList<String>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    class TagViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapter.TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_tags, parent, false)
        return TagViewHolder(view)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onBindViewHolder(holder: TagAdapter.TagViewHolder, position: Int) {

        val sharedPreferences = holder.view.context.applicationContext.getSharedPreferences(
            "com.musaguzel.anketgundem",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()


        var avd: AnimatedVectorDrawableCompat
        var avd2: AnimatedVectorDrawable
        val etiket = holder.view.etiket
        val done = holder.view.done
        val circle = holder.view.circle
        etiket.setText(tagNames[position])


        //Kaydedilen tıklanmış pozisyonları getirme
        val currrentPositionset = sharedPreferences.getStringSet("tagpositionset", hashSetOf())

        if (currrentPositionset != null) {
            AnaSayfaViewModel.TagList.tagPosition.addAll(currrentPositionset)  //objecti kaydedilen listeyle güncelleme

            if (currrentPositionset.contains(position.toString())){            //Eğerki tıklanmış etiket varsa görünürlükleri ayarla ve done animasyonunu baslat
                circle.visibility = View.VISIBLE
                done.visibility = View.VISIBLE
                etiket.animation = AnimationUtils.loadAnimation(holder.view.context,R.anim.enter_from_left)
                etiket.background = ContextCompat.getDrawable(holder.view.context,R.drawable.etiket_button_green)
                val drawable: Drawable = done.drawable
                if (drawable is AnimatedVectorDrawableCompat) {
                    avd = drawable
                    avd.start()
                }else if (drawable is AnimatedVectorDrawable){
                    avd2 = drawable
                    avd2.start()
                }
            }else{                                                               //Eğerki etiket daha önce tıklanmamışsa görünmez yap
                circle.visibility = View.INVISIBLE
                done.visibility = View.INVISIBLE
                etiket.background = ContextCompat.getDrawable(holder.view.context,R.drawable.etiket_button_blue)
            }
        }

        //Herhangi bir etiket tıklanırsa
        etiket.setOnClickListener {
            if (done.visibility == View.INVISIBLE && circle.visibility == View.INVISIBLE){ //Seçildi

                circle.visibility = View.VISIBLE
                done.visibility = View.VISIBLE                                                              //Viewleri görünür yap animasyon ayarla
                etiket.background = ContextCompat.getDrawable(it.context,R.drawable.etiket_button_green)
                etiket.animation = AnimationUtils.loadAnimation(it.context,R.anim.enter_from_left)


                AnaSayfaViewModel.TagList.taglist.add(etiket.text.toString())
                AnaSayfaViewModel.TagList.tagPosition.add(position.toString())
                editor.putStringSet("tagset",  AnaSayfaViewModel.TagList.taglist).apply()                //Taglist ekleme yap sharedpreferencesa kaydet
                editor.putStringSet("tagpositionset",  AnaSayfaViewModel.TagList.tagPosition).apply()
                println("setsecildi " + sharedPreferences.getStringSet("tagset", hashSetOf()) + " " + sharedPreferences.getStringSet("tagpositionset",
                    hashSetOf()))

                val drawable: Drawable = done.drawable
                if (drawable is AnimatedVectorDrawableCompat) {
                    avd = drawable
                    avd.start()                                                                             //Animasyon başlat
                }else if (drawable is AnimatedVectorDrawable){
                    avd2 = drawable
                    avd2.start()
                }
            }else{                                                                  //Seçilmeyi bıraktı
                etiket.background = ContextCompat.getDrawable(it.context,R.drawable.etiket_button_blue)
                circle.animation = AnimationUtils.loadAnimation(it.context,R.anim.exit_to_right)                    //Görünürlükleri Ayarla
                circle.visibility = View.INVISIBLE
                done.visibility = View.INVISIBLE

                AnaSayfaViewModel.TagList.taglist.remove(etiket.text.toString())
                AnaSayfaViewModel.TagList.tagPosition.remove(position.toString())
                editor.putStringSet("tagset",  AnaSayfaViewModel.TagList.taglist).apply()                   //Taglist ekleme yap sharedpreferencesa kaydet
                editor.putStringSet("tagpositionset",  AnaSayfaViewModel.TagList.tagPosition).apply()
                println("setsecildi " + sharedPreferences.getStringSet("tagset", hashSetOf()) + " " + sharedPreferences.getStringSet("tagpositionset",
                    hashSetOf()))
            }
        }
    }

    override fun getItemCount(): Int {
        return tagNames.size
    }

}