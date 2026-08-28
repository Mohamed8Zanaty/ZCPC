package com.example.zcpc.data.repository

import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.core.network.safeApiCall
import com.example.zcpc.data.codeforces.remote.CodeforcesApi
import com.example.zcpc.data.local.dao.UserDao
import com.example.zcpc.data.local.entity.toDomain
import com.example.zcpc.data.local.entity.toEntity
import com.example.zcpc.data.mapper.toDomain
import com.example.zcpc.domain.model.UserProfile
import com.example.zcpc.domain.repository.CodeforcesRepository
import javax.inject.Inject

class CodeforcesRepositoryImpl @Inject constructor(
    private val api: CodeforcesApi,
    private val userDao: UserDao
) : CodeforcesRepository {
    override suspend fun getUserProfile(handle: String): NetworkResult<UserProfile> {
        val networkResponse = safeApiCall { api.getUserInfo(handle) }

        if (networkResponse is NetworkResult.Success) {
            val data = networkResponse.data
            if (data.status == "OK" && !data.result.isNullOrEmpty()) {
                val domainUser = data.result.first().toDomain()
                userDao.insertUser(domainUser.toEntity())
                return NetworkResult.Success(domainUser)
            }
        }
        val cachedUser = userDao.getUser(handle)
        return if (cachedUser != null) {
            NetworkResult.Success(cachedUser.toDomain())
        } else {
            NetworkResult.Error(code = 503, message = "No internet connection and no cached data available.")
        }
    }
}