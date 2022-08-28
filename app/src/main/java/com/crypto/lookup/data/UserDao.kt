package com.crypto.lookup.data

import com.crypto.lookup.data.listeners.onGetDataListener
import com.crypto.lookup.data.listeners.onGetNoDataListener
import com.crypto.lookup.data.listeners.onSaveDataListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference

interface UserDao {
    fun save(user: User, password: String, listener: onSaveDataListener)
    fun retrieve(email: String, listener: onGetDataListener)
    fun loginAuth(email: String, password: String, listener: onSaveDataListener)
    fun currentUser(): FirebaseUser?
    fun unsubscribeCoin(email: String, symbol: String, listener: onSaveDataListener)
    fun userCollection(): CollectionReference
    fun subscribeCoin(email: String, symbol: String, listener: onSaveDataListener)
    fun updateEmail(newEmail: String, currentUser: User, listener: onSaveDataListener)
    fun updatePassword(newPassword: String, listener: onSaveDataListener)
    fun checkPassword(oldPassword: String, listener: onSaveDataListener)
    fun updateToken(user: User, newToken: String)
    fun resetPassword(email: String, listener: onGetNoDataListener)
}