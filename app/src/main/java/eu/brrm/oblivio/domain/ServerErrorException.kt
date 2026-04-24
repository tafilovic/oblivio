package eu.brrm.oblivio.domain

/**
 * Failure from an API call with text intended for the user (often parsed from a JSON [message] or [error] field).
 * UI may show [message] directly; [statusCode] is available to map to localized strings or analytics later.
 */
class ServerErrorException(
    override val message: String,
    val statusCode: Int? = null,
) : Exception(message)
