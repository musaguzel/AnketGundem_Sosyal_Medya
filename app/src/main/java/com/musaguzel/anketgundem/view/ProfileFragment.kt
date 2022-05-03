package com.musaguzel.anketgundem.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.adapter.UserPostAdapter
import com.musaguzel.anketgundem.model.Posts
import com.musaguzel.anketgundem.util.getImageFromFirebase
import com.musaguzel.anketgundem.util.placeholderShimmer
import com.musaguzel.anketgundem.viewmodel.ProfileViewModel
import com.musaguzel.anketgundem.viewmodel.UploadViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ana_sayfa.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var firebaseUser: FirebaseUser
    private var postAdapter = UserPostAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = context?.applicationContext?.getSharedPreferences(
            "com.musaguzel.anketgundem",
            Context.MODE_PRIVATE
        )

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.refreshUserInfo()

        profileRecyclerView.layoutManager = LinearLayoutManager(context)
        profileRecyclerView.adapter = postAdapter

        UploadViewModel.tagPositions.positions.clear()

        storage = Firebase.storage
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        observeLiveData()

        profileImageSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        profileButtonGoEdit.setOnClickListener {

            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            findNavController().navigate(action)
        }
    }



    fun observeLiveData(){
        viewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
            userInfo.let {
                if (it != null) {
                    profileTxtName.text = it.nameSurname
                    profileTxtUsername.text = it.userName
                    if (it.photoUrl != "") {
                        profileCirclePhoto.getImageFromFirebase(
                            it.photoUrl,
                            placeholderShimmer(requireContext())
                        )
                    }
                }
                //findNavController().currentDestination?.label = it.userName
            }
        })
        viewModel.userPosts.observe(viewLifecycleOwner, Observer { userPosts ->
            userPosts?.let {
                postAdapter.updateUserPostList(userPosts as ArrayList<Posts>)
            }
        })
    }
}