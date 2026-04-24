package eu.brrm.oblivio.data.remote.dto

import com.squareup.moshi.Json

data class SelfUserDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "username") val username: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "displayName") val displayName: String? = null,
    @Json(name = "display_name") val displayNameSnake: String? = null,
) {
    fun displayOrUsername(): String? = displayName ?: displayNameSnake ?: name ?: username
}
