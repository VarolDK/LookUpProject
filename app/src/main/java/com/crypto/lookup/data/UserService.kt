package com.crypto.lookup.data

import com.crypto.lookup.data.listeners.onGetDataListener
import com.crypto.lookup.data.listeners.onGetNoDataListener
import com.crypto.lookup.data.listeners.onSaveDataListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference

class UserService(private val userDao: UserDao) {

    fun save(user: User, password: String, listener: onSaveDataListener) {
        return userDao.save(user, password, listener)
    }

    fun retrieve(email: String, listener: onGetDataListener) {
        return userDao.retrieve(email, listener)
    }


    fun loginAuth(email: String, password: String, listener: onSaveDataListener) {
        return userDao.loginAuth(email, password, listener)
    }

    fun currentUser(): FirebaseUser? {
        return userDao.currentUser()
    }

    fun unsubscribeCoin(email: String, symbol: String, listener: onSaveDataListener) {
        userDao.unsubscribeCoin(email, symbol, listener)
    }

    fun userCollection(): CollectionReference {
        return userDao.userCollection()
    }

    fun subscribeCoin(email: String, symbol: String, listener: onSaveDataListener) {
        userDao.subscribeCoin(email, symbol, listener)
    }

    fun updateEmail(newEmail: String, currentUser: User, listener: onSaveDataListener) {
        userDao.updateEmail(newEmail, currentUser, listener)
    }

    fun updatePassword(newPassword: String, listener: onSaveDataListener) {
        userDao.updatePassword(newPassword, listener)
    }

    fun checkPassword(oldPassword: String, listener: onSaveDataListener) {
        userDao.checkPassword(oldPassword, listener)
    }

    fun updateToken(user: User, newToken: String) {
        userDao.updateToken(user, newToken)
    }

    fun resetPassword(email: String, listener: onGetNoDataListener) {
        userDao.resetPassword(email, listener)
    }
}