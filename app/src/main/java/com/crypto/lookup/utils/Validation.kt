package com.crypto.lookup.utils

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import java.sql.Date
import java.time.LocalDate
import java.util.*

class Validation {
    companion object {
        fun isEmailValid(email: String): Boolean {
            return if (email.isNullOrEmpty()) false
            else Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isPhoneValid(phone: String): Boolean {
            return if (phone.isNullOrEmpty()) false
            else Patterns.PHONE.matcher(phone).matches()
        }

        fun isTextValid(text: String, max: Int, min: Int): Boolean {
            return !(text.isNullOrEmpty() || text.length > max || text.length < min)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun isBirthdateValid(bd: Calendar): Boolean {
            return bd.time.before(Date.valueOf(LocalDate.now().minusYears(18).toString()))
        }

    }
}