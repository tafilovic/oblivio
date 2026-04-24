package eu.brrm.oblivio.data.remote

import com.squareup.moshi.JsonReader
import okio.Buffer

/**
 * Extracts a single user-facing string from common API error JSON, e.g.
 * `{"message":["..."],"error":"Bad Request","statusCode":400}` or a string [message].
 */
object ApiErrorMessageParser {

    fun parseUserMessage(errorBody: String): String? {
        if (errorBody.isBlank()) return null
        return runCatching { parseWithJsonReader(errorBody) }.getOrNull()
    }

    private fun parseWithJsonReader(json: String): String? {
        val reader = JsonReader.of(Buffer().writeUtf8(json))
        reader.isLenient = true
        if (reader.peek() != JsonReader.Token.BEGIN_OBJECT) return null
        reader.beginObject()
        var fromMessage: String? = null
        var fromError: String? = null
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "message" -> fromMessage = readMessageValue(reader)
                "error" -> {
                    if (reader.peek() == JsonReader.Token.STRING) {
                        fromError = reader.nextString()
                    } else {
                        reader.skipValue()
                    }
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        return fromMessage?.takeIf { it.isNotBlank() }
            ?: fromError?.takeIf { it.isNotBlank() }
    }

    private fun readMessageValue(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString()
            JsonReader.Token.BEGIN_ARRAY -> {
                reader.beginArray()
                val parts = buildList {
                    while (reader.hasNext()) {
                        if (reader.peek() == JsonReader.Token.STRING) {
                            add(reader.nextString())
                        } else {
                            reader.skipValue()
                        }
                    }
                }
                reader.endArray()
                parts.joinToString(" ")
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }
}
