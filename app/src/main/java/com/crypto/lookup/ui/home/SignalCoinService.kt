package com.crypto.lookup.ui.home

import com.crypto.lookup.data.listeners.onGetDataListListener
import com.crypto.lookup.data.listeners.onGetDataListener

class SignalCoinService(private val signalCoinDao: SignalCoinDao) {
    fun retrieve(signalCoin: ArrayList<String>, listener: onGetDataListListener) {
        signalCoinDao.retrieve(signalCoin, listener)
    }

    fun retrieveCoins(listener: onGetDataListener) {
        signalCoinDao.retrieveCoins(listener)
    }

    fun fakeData() {
        signalCoinDao.fakeData() // TODO DELETE
    }

    fun retrieveTweet(listener: onGetDataListListener) {
        signalCoinDao.retrieveTweet(listener)
    }

    fun retrieveTweetDaily(listener: onGetDataListListener) {
        signalCoinDao.retrieveTweetDaily(listener)
    }

    fun retrieveTweetSentDaily(listener: onGetDataListListener) {
        signalCoinDao.retrieveTweetSentDaily(listener)
    }
}