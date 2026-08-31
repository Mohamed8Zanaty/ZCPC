package com.example.zcpc.data.repository

import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.core.network.safeApiCall
import com.example.zcpc.data.codeforces.remote.CodeforcesApi
import com.example.zcpc.data.local.dao.ContestDao
import com.example.zcpc.data.local.dao.ProblemDao
import com.example.zcpc.data.local.dao.RivalDao
import com.example.zcpc.data.local.dao.UserDao
import com.example.zcpc.data.local.entity.toDomain
import com.example.zcpc.data.local.entity.toEntity
import com.example.zcpc.data.local.entity.toRivalEntity
import com.example.zcpc.data.mapper.toDomain
import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.ContestPhase
import com.example.zcpc.domain.model.SolvedProblem
import com.example.zcpc.domain.model.Submission
import com.example.zcpc.domain.model.UserProfile
import com.example.zcpc.domain.repository.CodeforcesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CodeforcesRepositoryImpl @Inject constructor(
    private val api: CodeforcesApi,
    private val userDao: UserDao,
    private val contestDao: ContestDao,
    private val problemDao: ProblemDao,
    private val rivalDao: RivalDao
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

    override suspend fun getContests(): NetworkResult<List<Contest>> {
        val response = safeApiCall { api.getContests() }
        if (response is NetworkResult.Success) {
            val data = response.data
            if (data.status == "OK" && data.result != null) {
                val domainContests = data.result
                    .map { it.toDomain() }
                    .take(50)

                contestDao.clearAndInsertContests(domainContests.map { it.toEntity() })
                return NetworkResult.Success(domainContests)
            }
        }
        val cachedContests = contestDao.getAllContests()
        return if (cachedContests.isNotEmpty()) {
            NetworkResult.Success(cachedContests.map { it.toDomain() })
        } else {
            NetworkResult.Error(code = 503, message = "No internet connection and no cached contests available.")
        }
    }

    override suspend fun getSolvedProblems(handle: String): NetworkResult<List<SolvedProblem>> {
        val response = safeApiCall { api.getUserStatus(handle) }

        if (response is NetworkResult.Success) {
            val data = response.data
            if (data.status == "OK" && data.result != null) {
                val uniqueSolvedProblems = data.result
                    .filter { it.verdict == "OK" }
                    .distinctBy { it.problem.name }
                    .map { dto ->
                        SolvedProblem(
                            contestId = dto.problem.contestId,
                            index = dto.problem.index,
                            name = dto.problem.name,
                            rating = dto.problem.rating ?: 0,
                            tags = dto.problem.tags
                        )
                    }

                problemDao.clearAndInsertProblems(uniqueSolvedProblems.map { it.toEntity() })
                return NetworkResult.Success(uniqueSolvedProblems)
            }
        }
        val cachedProblems = problemDao.getAllSolvedProblems()
        return if (cachedProblems.isNotEmpty()) {
            NetworkResult.Success(cachedProblems.map { it.toDomain() })
        } else {
            NetworkResult.Error(
                code = 503,
                message = "No internet connection and no cached problems available."
            )
        }
    }

    override suspend fun getSubmissions(handle: String): NetworkResult<List<Submission>> {
        val response = safeApiCall { api.getUserStatus(handle) }

        return if (response is NetworkResult.Success) {
            val data = response.data
            if (data.status == "OK" && data.result != null) {
                NetworkResult.Success(data.result.map { it.toDomain() })
            } else {
                NetworkResult.Error(code = 404, message = "User not found or no submissions.")
            }
        } else {
            // We could cache submissions too, but for now let's just return error if offline
            NetworkResult.Error(code = 503, message = "No internet connection.")
        }
    }

    override fun getRivalsFlow(): Flow<List<UserProfile>> {
        return rivalDao.getAllRivalsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addRival(handle: String): NetworkResult<Unit> {
        val response = safeApiCall { api.getUserInfo(handle) }

        return when (response) {
            is NetworkResult.Success -> {
                val data = response.data
                if (data.status == "OK" && !data.result.isNullOrEmpty()) {
                    val dto = data.result.first()
                    val profile = dto.toDomain()
                    rivalDao.insertRival(profile.toRivalEntity())
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(404, "Handle not found")
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(response.code, response.message)
            is NetworkResult.Exception -> NetworkResult.Exception(response.e)
        }
    }

    override suspend fun removeRival(profile: UserProfile) {
        rivalDao.deleteRival(profile.toRivalEntity())
    }
}