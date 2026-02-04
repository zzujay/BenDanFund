package com.bendanfund.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface FundApiService {

    @GET("fund/detail")
    suspend fun getFundDetail(
        @Query("fund_code") fundCode: String
    ): ApiResponse<FundDetailResponse>

    @GET("fund/estimate")
    suspend fun getFundEstimate(
        @Query("fund_code") fundCode: String
    ): ApiResponse<EstimatedValueResponse>

    @GET("fund/position")
    suspend fun getFundPosition(
        @Query("fund_code") fundCode: String
    ): ApiResponse<FundPositionResponse>

    @GET("fund/batch-estimate")
    suspend fun getBatchEstimate(
        @Query("fund_codes") fundCodes: String
    ): ApiResponse<List<EstimatedValueResponse>>
}
