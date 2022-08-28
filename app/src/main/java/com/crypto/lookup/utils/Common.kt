package com.crypto.lookup.utils

import java.text.SimpleDateFormat
import java.util.*

class Common {
    companion object {
        fun dateFormat(calendar: Calendar): String {
            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            return sdf.format(calendar.getTime())
        }

    }
}
