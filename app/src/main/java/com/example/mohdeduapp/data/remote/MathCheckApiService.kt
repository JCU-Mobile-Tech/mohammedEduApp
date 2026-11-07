package com.example.mohdeduapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MathCheckApiService {
    @GET("/")
    suspend fun checkMath(@Query("expr") expr: String): String
}
