package com.bendanfund.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FundApiService {

    @GET("js")
    suspend fun getFundEstimate(
        @Query("fundCode") fundCode: String,
        @Query("type") type: String = "gz"
    ): String

    @GET("js")
    suspend fun getBatchEstimate(
        @Query("fundCodes") fundCodes: String,
        @Query("type") type: String = "gz"
    ): List<String>
}
