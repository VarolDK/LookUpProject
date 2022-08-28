package com.crypto.lookup.ui.home

import com.crypto.lookup.data.listeners.onGetDataListListener
import com.crypto.lookup.data.listeners.onGetDataListener

interface SignalCoinDao {
    fun retrieve(signalCoin: ArrayList<String>, listener: onGetDataListListener)
    fun fakeData() // TODO DELETE
    fun retrieveCoins(listener: onGetDataListener)
    fun retrieveTweet(listener: onGetDataListListener)
    fun retrieveTweetDaily(listener: onGetDataListListener)
    fun retrieveTweetSentDaily(listener: onGetDataListListener)
}