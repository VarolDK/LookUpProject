package com.crypto.lookup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crypto.lookup.data.User
import com.crypto.lookup.data.UserFirebaseDaoImpl
import com.crypto.lookup.data.UserService
import com.crypto.lookup.data.listeners.onGetDataListener
import com.crypto.lookup.databinding.ActivityEntryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase

class EntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private val db = UserService(UserFirebaseDaoImpl())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            db.retrieve(currentUser.email.toString(), object : onGetDataListener {
                override fun onSuccess(data: DocumentSnapshot) {
                    val user = data.toObject(User::class.java)!!
                    val intent = Intent(this@EntryActivity, MainActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("user", user)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    this@EntryActivity.finish()
                }

                override fun onFailed(e: Exception) {

                }
            })
        } else {
            binding = ActivityEntryBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }

    }
}