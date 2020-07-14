package org.albaspazio.core.updater

import android.util.Base64
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class AuthenticationOptions(options: JSONObject?) {
    var authType: String? = null
    var username: String? = null
    var password: String? = null

    init {
        if(options != null) {
            try {
                authType = options.getString("authType")
                username = options.getString("username")
                password = options.getString("password")
            } catch (e: JSONException) {
                // If there is any error then ensure that auth type is unset
                authType = ""
            }
        }
    }

    /**
     * Flag indicating authentication credentials have been set
     *
     * @return boolean flag indicating if there are authentication credentials
     */
    fun hasCredentials(): Boolean {
        return authType == "basic"
    }

    val encodedAuthorization: String
        get() = "Basic " + Base64.encodeToString(("$username:$password").toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)

}