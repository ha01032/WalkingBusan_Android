package com.example.walking.retrofit


import com.example.walking.model.Walking
import retrofit2.Call
import retrofit2.http.*


interface NetworkService {


    @GET("WalkingService/getWalkingKr")
    fun getList(
        @Query("serviceKey") serviceKey: String?,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("resultType") resultType: String?
    ): Call<Walking>

}