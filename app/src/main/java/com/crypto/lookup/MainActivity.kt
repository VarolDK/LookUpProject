package com.crypto.lookup

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crypto.lookup.data.User
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.databinding.ActivityMainBinding
import com.crypto.lookup.ui.login.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: UserViewModel
    private lateinit var userService: UserService

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {

        sharedViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val user = intent?.extras?.getSerializable("user")
        sharedViewModel.setCurrentUser(user as User)

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        navView.itemActiveIndicatorColor

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        userService = UserService(UserFirebaseDaoImpl())

        val newToken = getSharedPreferences(this.packageName, Context.MODE_PRIVATE).getString("TOKEN", "DEFAULT")!!
        if (newToken != "DEFAULT" && newToken != user.phoneID) {
            userService.updateToken(user, newToken)

        }

        userService.userCollection().document(user.email).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Listening", "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
                val snapshotuser = snapshot.toObject(User::class.java)!!
                sharedViewModel.setCurrentUser(snapshotuser)
            } else {
                Log.d("Listening", "$source data: null")
            }

        }

    }

}