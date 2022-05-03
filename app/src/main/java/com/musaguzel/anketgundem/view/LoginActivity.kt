package com.musaguzel.anketgundem.view

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.musaguzel.anketgundem.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null){
            checkCurrentUser()
        }

        startTransaction()

    }



    fun checkCurrentUser(){
        val intent = Intent(this,MainActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    fun setButtonBackground(
        clicked: FrameLayout,
        not1: FrameLayout,
        not2: FrameLayout,
        clickedText: TextView,
        notClicked1: TextView,
        notClicked2: TextView
    ){
        clicked.background = ContextCompat.getDrawable(this, R.drawable.button_rectangle)
        not1.setBackgroundResource(0)
        not2.setBackgroundResource(0)

        clickedText.setTextColor(Color.BLACK)
        notClicked1.setTextColor(Color.parseColor("#786464"))
        notClicked2.setTextColor(Color.parseColor("#786464"))
    }

    fun startTransaction(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val loginFragment = LoginFragment()
        fragmentTransaction.replace(R.id.replaceFrameLayout, loginFragment).commit()
    }
    fun login(view: View){
        setButtonBackground(
            frameLayoutLogin,
            frameLayoutSignup,
            frameLayoutForgotPass,
            txtLogin,
            txtSignup,
            txtForgotPass
        )

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val loginFragment = LoginFragment()
        fragmentTransaction.replace(R.id.replaceFrameLayout, loginFragment).commit()


    }

    fun signup(view: View){
        setButtonBackground(
            frameLayoutSignup,
            frameLayoutLogin,
            frameLayoutForgotPass,
            txtSignup,
            txtLogin,
            txtForgotPass
        )

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val registerFragment = RegisterFragment()
        fragmentTransaction.replace(R.id.replaceFrameLayout, registerFragment).commit()

    }
    fun forgotpass(view: View){
        setButtonBackground(
            frameLayoutForgotPass,
            frameLayoutSignup,
            frameLayoutLogin,
            txtForgotPass,
            txtLogin,
            txtSignup
        )

    }
}