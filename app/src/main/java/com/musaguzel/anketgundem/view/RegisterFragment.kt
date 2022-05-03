package com.musaguzel.anketgundem.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {

    private lateinit var viewmodel:RegisterViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        db = Firebase.firestore
        auth = Firebase.auth
        register()
    }
    fun goLoginFragment(){
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()

        val loginFragment = LoginFragment()

        fragmentTransaction?.replace(R.id.replaceFrameLayout, loginFragment)?.commit()

    }

    fun register(){
        buttonRegister.setOnClickListener {
            val name = edtTxtName.text.toString()
            val username = edtTxtUserName.text.toString()
            val email = edtTxtEmail.text.toString()
            val password = edtTxtPass.text.toString()
            val password2 = edtTxtPass2.text.toString()

            if (name.equals("") || email.equals("") || password.equals("") || password2.equals("")){
                Toast.makeText(context, "Lütfen Bilgileri Eksiksiz Girin", Toast.LENGTH_SHORT).show()
            }else if (password != password2){
                Toast.makeText(context, "Şifreler Uyuşmuyor", Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {

                    val infoMap = HashMap<String,Any>()
                    infoMap.put("nameSurname",name)
                    infoMap.put("userName",username)
                    infoMap.put("email",email)
                    saveUserInfo(infoMap)

                    Toast.makeText(context,"Kayıt Başarılı",Toast.LENGTH_LONG).show()
                    goLoginFragment()
                }.addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun saveUserInfo(infoMap:HashMap<String,Any>){

        auth.currentUser?.let {
            db.collection("Users").document(it.uid).set(infoMap).addOnSuccessListener {

            }.addOnFailureListener {
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }




}