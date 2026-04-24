package eu.brrm.oblivio.data.remote

import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory auth material for [AuthRequestInterceptor] (must not use suspend / DataStore on the OkHttp thread).
 */
@Singleton
class AuthSessionMemory @Inject constructor() {
    @Volatile
    var bearerToken: String? = null
        private set

    @Volatile
    var cookieHeader: String? = null
        private set

    fun setSession(bearer: String?, cookie: String?) {
        bearerToken = bearer
        cookieHeader = cookie
    }

    fun clear() {
        bearerToken = null
        cookieHeader = null
    }
}
