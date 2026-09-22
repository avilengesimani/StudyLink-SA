package com.example.studylinksa

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApiService {

    @GET("api.php")
    fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("difficulty") difficulty: String = "easy",
        @Query("type") type: String = "multiple"
    ): Call<QuizResponse>
}