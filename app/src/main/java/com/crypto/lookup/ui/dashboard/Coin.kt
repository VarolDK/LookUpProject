package com.crypto.lookup.ui.dashboard

data class CoinList(val coins: ArrayList<Coin>)
data class Coin(
    val name: String,
    var price: Float,
//    var tweetCount: Int
)