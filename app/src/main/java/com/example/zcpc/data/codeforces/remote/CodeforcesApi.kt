package com.example.zcpc.data.codeforces.remote

import com.example.zcpc.data.codeforces.remote.dto.CodeforcesResponse
import com.example.zcpc.data.codeforces.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CodeforcesApi {
    companion object {
        const val BASE_URL = "https://codeforces.com/api/"
    }

    @GET("user.info")
    suspend fun getUserInfo(
        @Query("handles") handles: String
    ): CodeforcesResponse<List<UserDto>>
}