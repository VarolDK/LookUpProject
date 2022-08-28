package com.crypto.lookup.data

import java.util.*

data class Fear(
    var index: Int,
    var classification: String,
)

data class Tweet(
    var symbol: String = "",
    @field:JvmField
    var tweet_count: Int = 0,
    var start: Date? = null,
    var end: Date? = null
)

data class TweetSent(
    var symbol: String = "",
    var positive: Int = 0,
    var negative: Int = 0,
    var neutral: Int = 0,
    var start: Date? = null,
    var end: Date? = null
)