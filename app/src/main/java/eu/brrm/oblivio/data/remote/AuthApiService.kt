package eu.brrm.oblivio.data.remote

import eu.brrm.oblivio.data.remote.dto.LoginRequestDto
import eu.brrm.oblivio.data.remote.dto.SelfUserDto
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<ResponseBody>

    @POST("users")
    suspend fun registerUser(@Body body: RequestBody): Response<ResponseBody>

    @GET("users/me/")
    suspend fun getSelf(): Response<SelfUserDto>
}
