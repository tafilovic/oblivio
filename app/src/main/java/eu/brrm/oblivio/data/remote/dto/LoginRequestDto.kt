package eu.brrm.oblivio.data.remote.dto

/**
 * JSON body for [AuthApiService.login]; field names match the backend contract.
 */
data class LoginRequestDto(
    val identifier: String,
    val password: String,
)
