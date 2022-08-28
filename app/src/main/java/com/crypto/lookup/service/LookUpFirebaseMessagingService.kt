package com.crypto.lookup.service

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService

class LookUpFirebaseMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val sharedPreferences =
            applicationContext.getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("TOKEN", token)
        editor.commit()

    }




}