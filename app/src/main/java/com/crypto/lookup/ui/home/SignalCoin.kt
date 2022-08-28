package com.crypto.lookup.ui.home

import com.google.firebase.firestore.Exclude
import java.util.*

class SignalCoinList(val signalCoins: ArrayList<SignalCoin>)

data class SignalCoin(
    var symbol: String = "",
    var openPrice: Float = 0F,
    var closePrice: Float? = 0F,
    var openDate: Date? = null,
    var closeDate: Date? = null,
    @field:JvmField
    var isOpen: Boolean = true,
    var lastStopPrice: Float = 0F,
    @get:Exclude
    var currentPrice: Float = 0F,
)