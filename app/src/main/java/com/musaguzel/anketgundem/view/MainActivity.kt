package com.musaguzel.anketgundem.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.musaguzel.anketgundem.R
import com.musaguzel.anketgundem.util.ClearCache
import com.musaguzel.anketgundem.viewmodel.AnaSayfaViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(){

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavigationView: BottomNavigationView

    private var sharedPreferences: SharedPreferences? = null

    val selectedIndex = AnaSayfaViewModel.SelectedPosition
    val selectedList = selectedIndex.selectedIndexList

    override fun onCreate(savedInstanceState: Bundle?) {
        checkDarkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sharedPreferences = this.applicationContext?.getSharedPreferences(
            "com.musaguzel.anketgundem",
            Context.MODE_PRIVATE)


        val myWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ClearCache>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueue(myWorkRequest)

        val uuidhashset = HashSet<String>()
        val uuidandtextidhashset = HashSet<String>()

        var kontrol: HashSet<String>? = null
        var kontrol2: HashSet<String>? = null
        var kontrol3: HashSet<String>? = null

        kontrol = sharedPreferences?.getStringSet("uuidset", uuidhashset) as HashSet<String>?
        kontrol2 = sharedPreferences?.getStringSet("uuidandtextid", uuidandtextidhashset) as HashSet<String>?
        kontrol3 = sharedPreferences?.getStringSet("tagset", hashSetOf()) as HashSet<String>
        if (kontrol != null || kontrol2 != null) {

            selectedList.clear()
            selectedIndex.selectedTextViewIndex.clear()
            kontrol?.let { selectedList.addAll(it) }
            kontrol2?.let { selectedIndex.selectedTextViewIndex.addAll(it) }
            //sharedPreferences?.getStringSet("uuidset", uuidhashset)?.let { selectedList.addAll(it) }

        }


        setSupportActionBar(toolactionbar)


        //BottomNavigation ile fragment bağlama '' ActionBarın fragment adlarına dönüştürme


        val navHostFragment = this.supportFragmentManager.findFragmentById(
            R.id.fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        //bottomNavigationView.setupWithNavController(navController)  * Fragmentlar arası eski gecis yontemi
        bottomnavigationListener()


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.anaSayfaFragment,
                R.id.uploadFragment,
                R.id.profileFragment,
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        hideActionBar()

        etiketlerText.setOnClickListener {
            navController.navigate(R.id.action_anaSayfaFragment_to_interestTagsFragment)
        }

    }

    fun bottomnavigationListener(){
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.anaSayfaFragment -> {
                    etiketlerText.visibility = View.VISIBLE
                    navController.navigate(R.id.anaSayfaFragment,null, navOptions {
                        anim {
                            enter = R.anim.fadein
                            exit = R.anim.fadeout
                        }
                    })
                }
                R.id.uploadFragment -> {
                    etiketlerText.visibility = View.GONE
                    navController.navigate(R.id.uploadFragment,null, navOptions {
                        anim {
                            enter = R.anim.fadein
                            exit = R.anim.fadeout
                        }
                    })
                }
                R.id.profileFragment -> {
                    etiketlerText.visibility = View.GONE
                    navController.navigate(R.id.profileFragment,null, navOptions {
                        anim {
                            enter = R.anim.fadein
                            exit = R.anim.fadeout
                        }
                    })
                }
            }

            return@setOnItemSelectedListener true
        }
    }


    fun checkDarkMode(){
        val sharedPreferencess : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightMode = sharedPreferencess.getBoolean("darkmode",false)
        if (nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_Dark)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_Light)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController: NavController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    fun hideActionBar(){
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.profileFragment){
                appBarLayout2.visibility = View.GONE
            }else {
                appBarLayout2.visibility = View.VISIBLE
            }
            if(destination.id == R.id.interestTagsFragment){
                etiketlerText.visibility = View.GONE
                bottomNavigationView.visibility = View.GONE
            }else if (destination.id == R.id.anaSayfaFragment){
                etiketlerText.visibility = View.VISIBLE
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

}