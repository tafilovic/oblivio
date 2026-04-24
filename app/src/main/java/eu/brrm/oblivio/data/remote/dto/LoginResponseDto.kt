package eu.brrm.oblivio.data.remote.dto

import com.squareup.moshi.Json

/**
 * Tolerant to backend shape: flat or nested `data`, snake_case or camelCase, common aliases.
 */
data class LoginResponseDto(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "accessToken") val accessTokenCamel: String? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "refreshToken") val refreshTokenCamel: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "access") val access: String? = null,
    @Json(name = "data") val data: LoginResponseData? = null,
) {
    fun accessTokenValue(): String? =
        firstNonBlank(
            accessToken,
            accessTokenCamel,
            token,
            access,
            data?.accessToken,
            data?.accessTokenCamel,
            data?.token,
            data?.access,
        )

    fun refreshTokenValue(): String? =
        firstNonBlank(
            refreshToken,
            refreshTokenCamel,
            data?.refreshToken,
            data?.refreshTokenCamel,
        )

    private fun firstNonBlank(vararg values: String?): String? =
        values.firstOrNull { !it.isNullOrBlank() }
}

data class LoginResponseData(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "accessToken") val accessTokenCamel: String? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "refreshToken") val refreshTokenCamel: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "access") val access: String? = null,
)
