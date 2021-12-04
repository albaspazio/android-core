package org.albaspazio.core.accessory

import android.graphics.Color
import java.security.MessageDigest
import java.util.regex.Pattern

/*
val md5Hash = "test".md5 // 098f6bcd4621d373cade4e832627b4f6
val sha1Hash = "test".sha1 // a94a8fe5ccb19ba61c4c0873d391e987982fbbd3
 */

val String.md5: String
    get() {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

val String.sha1: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }


fun String.isEmailValid(): Boolean {
    val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}


/*
val ccFormatted = "1234567890123456".creditCardFormatted // "1234 5678 9012 3456"
 */

val String.creditCardFormatted: String
    get() {
        val preparedString = replace(" ", "").trim()
        val result = StringBuilder()
        for (i in preparedString.indices) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ")
            }
            result.append(preparedString[i])
        }
        return result.toString()
    }


//import android.content.Context
//import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
//
//fun String.formatPhoneNumber(context: Context, region: String): String? {
//    val phoneNumberKit = PhoneNumberUtil.createInstance(context)
//    val number = phoneNumberKit.parse(this, region)
//    if (!phoneNumberKit.isValidNumber(number))
//        return null
//
//    return phoneNumberKit.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
//}

val String.containsLatinLetter: Boolean
    get() = matches(Regex(".*[A-Za-z].*"))

val String.containsDigit: Boolean
    get() = matches(Regex(".*[0-9].*"))

val String.isAlphanumeric: Boolean
    get() = matches(Regex("[A-Za-z0-9]*"))

val String.hasLettersAndDigits: Boolean
    get() = containsLatinLetter && containsDigit

val String.isIntegerNumber: Boolean
    get() = toIntOrNull() != null

val String.toDecimalNumber: Boolean
    get() = toDoubleOrNull() != null


/*
Let’s say we need to check if a password is valid. This is for a new password, not verifying an existing one.
The new password requires one digit, one Latin letter, and one non-alphanumeric character.
Also, we don’t want spaces in password, except first and last characters, we just trim them.
The last validation — length. We want our password to contain six to 20 characters.
val password = "yt6Hbb2.s(ma**213"
val password2 = "yt6Hbb2sma213"
val isPasswordValid = !password.isAlphanumeric && password.containsDigit &&
                       password.containsLatinLetter && password.length > 6 &&
                       password.length < 20 // true
val isPassword2Valid = !password2.isAlphanumeric && password2.containsDigit &&
                        password2.containsLatinLetter && password2.length > 6
                        && password2.length < 20 // false, doesn't contain non-alphanumeric characters
 */


/*
val c = "#010203".awtColor
print(c.toString()) // java.awt.Color[r=1,g=2,b=3]

val colorHex = "#010203"
val color = colorHex.asColor // -16711165
val nonColorHex = "abcdef"
val nonColor = nonColorHex.asColor // null
 */

val String.asColor: Int?
    get() = try {
        Color.parseColor(this)
    } catch (e: java.lang.IllegalArgumentException) {
        null
    }