package com.example.mohdeduapp.di

import android.content.Context
import com.example.mohdeduapp.data.remote.MathCheckApiService
import com.example.mohdeduapp.data.repository.MathCheckRepository
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class AppContainer(context: Context) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.mathjs.org/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val mathCheckApiService = retrofit.create(MathCheckApiService::class.java)

    val mathCheckRepository = MathCheckRepository(mathCheckApiService)
}
