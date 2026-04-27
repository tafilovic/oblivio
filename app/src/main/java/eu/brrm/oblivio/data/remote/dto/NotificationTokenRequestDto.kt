package eu.brrm.oblivio.data.remote.dto

data class NotificationTokenRequestDto(
    val token: String,
    val platform: String,
    val deviceId: String,
)
