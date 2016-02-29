package com.kamedon.todo.util

import com.kamedon.todo.BuildConfig
import okhttp3.internal.Util
import java.security.MessageDigest

/**
 * Created by kamedon on 2/27/16.
 */
object XUserAgentAuthorizationUtil {
    fun token(): String {
        return Util.md5Hex(BuildConfig.X_USER_AGENT_AUTHORIZATION_TOKEN);
    }
}
