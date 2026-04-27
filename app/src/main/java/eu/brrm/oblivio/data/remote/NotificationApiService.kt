package eu.brrm.oblivio.data.remote

import eu.brrm.oblivio.data.remote.dto.NotificationTokenRequestDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApiService {
    @POST("notifications/token")
    suspend fun subscribeDevice(@Body body: NotificationTokenRequestDto): Response<ResponseBody>
}
