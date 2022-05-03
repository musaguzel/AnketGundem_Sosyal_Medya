package com.musaguzel.anketgundem.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.musaguzel.anketgundem.R
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        login()

    }





    fun login() {
        buttonLogin.setOnClickListener {
            val email = edtTxtEmailLoginFragment.text.toString()
            val password = edtTxtPassLoginFragment.text.toString()

            if (email.equals("") || password.equals("")) {
                Toast.makeText(context, "Lütfen Bilgileri Eksiksiz Girin", Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                    Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_LONG).show()
                    val options = ActivityOptions.makeSceneTransitionAnimation(activity)
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    activity?.startActivity(intent, options.toBundle())


                }.addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}