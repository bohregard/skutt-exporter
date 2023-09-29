package com.bohregard.web

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SkuttApi {
    @FormUrlEncoded
    @POST("https://263chzc7pa.execute-api.us-west-2.amazonaws.com/prod/users/login")
    suspend fun getAuth(
        @Field("email") email: String,
        @Field("password") password: String,
    ): AuthResponse

    @Headers("x-app-name-token: kiln-link")
    @POST("kilns/view")
    suspend fun getKilns(@Header("x-access-token") token: String, @Body kilnRequest: KilnRequest): KilnResponse
}