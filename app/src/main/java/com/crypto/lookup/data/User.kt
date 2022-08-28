package com.crypto.lookup.data

import java.io.Serializable
import java.util.*

data class User(
    var name: String = "",
    var surname: String = "",
    var identityNumber: Long = 0L,
    var phoneNumber: Long = 0L,
    var birthDate: Date? = null,
    var email: String = "",
    var phoneID: String = "",
    val subscribedCoins: ArrayList<String> = arrayListOf()
) : Serializable
