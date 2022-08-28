package com.crypto.lookup.data.listeners

import com.google.firebase.firestore.DocumentSnapshot

interface onGetDataListener {
    fun onSuccess(data: DocumentSnapshot)
    fun onFailed(e: Exception)
}

interface onGetDataListListener {
    fun onSuccess(data: List<DocumentSnapshot>)
    fun onFailed(e: Exception)
}

interface onGetNoDataListener {
    fun onSuccess()
    fun onFailed(e: Exception)
}

interface onSaveDataListener {
    fun onSuccess()
    fun onFailed(exception: Exception)
}

