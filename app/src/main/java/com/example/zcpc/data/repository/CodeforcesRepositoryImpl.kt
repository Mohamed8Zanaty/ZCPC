package com.example.zcpc.data.repository

import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.core.network.safeApiCall
import com.example.zcpc.data.codeforces.remote.CodeforcesApi
import com.example.zcpc.data.mapper.toDomain
import com.example.zcpc.domain.model.UserProfile
import com.example.zcpc.domain.repository.CodeforcesRepository
import javax.inject.Inject

class CodeforcesRepositoryImpl @Inject constructor(
    private val api: CodeforcesApi
) : CodeforcesRepository {
    override suspend fun getUserProfile(handle: String): NetworkResult<UserProfile> {
        val response = safeApiCall { api.getUserInfo(handle) }

        return when(response) {
            is NetworkResult.Success -> {
                val data = response.data
                if(data.status == "OK" && !data.result.isNullOrEmpty()) {
                    val domainUser = data.result.first().toDomain()
                    NetworkResult.Success(domainUser)
                } else {
                    NetworkResult.Error(code = 400, message = data.comment ?: "Unknown API Error")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(response.code, response.message)
            is NetworkResult.Exception -> NetworkResult.Exception(response.e)
        }
    }
}