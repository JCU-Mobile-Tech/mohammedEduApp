package com.example.mohdeduapp.data.repository

import com.example.mohdeduapp.data.remote.MathCheckApiService

class MathCheckRepository(
    private val apiService: MathCheckApiService
) {
    suspend fun evaluateExpression(expr: String): String {
        return try {
            apiService.checkMath(expr)
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}
