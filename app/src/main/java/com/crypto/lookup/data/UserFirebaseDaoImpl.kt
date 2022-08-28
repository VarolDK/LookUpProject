package com.crypto.lookup.data

import android.util.Log
import com.crypto.lookup.data.listeners.onGetDataListener
import com.crypto.lookup.data.listeners.onGetNoDataListener
import com.crypto.lookup.data.listeners.onSaveDataListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class UserFirebaseDaoImpl : UserDao {

    private val db = Firebase.firestore.collection("users")
    private val auth = FirebaseAuth.getInstance()
    private val messaging = FirebaseMessaging.getInstance()



    override fun save(user: User, password: String, listener: onSaveDataListener) {
        messaging.token.addOnSuccessListener { phoneID ->
            user.phoneID = phoneID
            db.document(user.email).set(user).addOnSuccessListener {
                createAuth(user, password, object : onSaveDataListener {
                    override fun onSuccess() {
                        listener.onSuccess()
                    }

                    override fun onFailed(exception: Exception) {
                        db.document(user.email).delete()
                        listener.onFailed(exception)
                    }
                })
            }.addOnFailureListener {
                listener.onFailed(it)
            }
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    private fun createAuth(user: User, password: String, listener: onSaveDataListener) {
        auth.createUserWithEmailAndPassword(user.email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onSuccess()
            } else {
                Log.w("AUTH", it.exception)
                listener.onFailed(it.exception!!)
            }
        }
    }

    override fun retrieve(email: String, listener: onGetDataListener) {
        try {
            db.document(email).get().addOnSuccessListener {
                if (it.data == null)
                    listener.onFailed(Exception())
                else
                    listener.onSuccess(it)
            }.addOnFailureListener {
                listener.onFailed(it)
            }
        } catch (e: Exception) {
            listener.onFailed(e)
        }
    }

    override fun loginAuth(email: String, password: String, listener: onSaveDataListener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onSuccess()
            } else {
                listener.onFailed(it.exception!!)
            }
        }
    }

    override fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun unsubscribeCoin(email: String, symbol: String, listener: onSaveDataListener) {
        db.document(email).update("subscribedCoins", FieldValue.arrayRemove(symbol)).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    override fun userCollection(): CollectionReference {
        return db
    }

    override fun subscribeCoin(email: String, symbol: String, listener: onSaveDataListener) {
        db.document(email).update("subscribedCoins", FieldValue.arrayUnion(symbol)).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    override fun updateEmail(newEmail: String, currentUser: User, listener: onSaveDataListener) {
        val oldEmail = currentUser.email
        currentUser.email = newEmail
        updateAuthEmail(newEmail, object : onSaveDataListener {
            override fun onSuccess() {
                db.document(newEmail).set(currentUser).addOnSuccessListener {
                    deleteOldUser(oldEmail, object : onSaveDataListener {
                        override fun onSuccess() {
                            listener.onSuccess()
                        }

                        override fun onFailed(exception: Exception) {
                            Log.w("DELETE", exception.stackTraceToString())
                            listener.onFailed(exception)
                        }
                    })
                }.addOnFailureListener {
                    Log.w("SET NEW USER", it.stackTraceToString())
                    listener.onFailed(it)
                }
            }

            override fun onFailed(exception: Exception) {
                Log.w("AUTH", exception.stackTraceToString())
                listener.onFailed(exception)
            }
        })


    }

    override fun updatePassword(newPassword: String, listener: onSaveDataListener) {
        val currentUser = auth.currentUser
        currentUser!!.updatePassword(newPassword)
        auth.updateCurrentUser(currentUser).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    override fun checkPassword(oldPassword: String, listener: onSaveDataListener) {
        auth.signInWithEmailAndPassword(auth.currentUser!!.email.toString(), oldPassword).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    override fun updateToken(user: User, newToken: String) {
        db.document(user.email).update("phoneID", newToken)
    }

    override fun resetPassword(email: String, listener: onGetNoDataListener) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    private fun updateAuthEmail(newEmail: String, listener: onSaveDataListener) {
        auth.currentUser!!.updateEmail(newEmail)
        auth.updateCurrentUser(auth.currentUser!!).addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }

    private fun deleteOldUser(oldEmail: String, listener: onSaveDataListener) {
        db.document(oldEmail).delete().addOnSuccessListener {
            listener.onSuccess()
        }.addOnFailureListener {
            listener.onFailed(it)
        }
    }


}