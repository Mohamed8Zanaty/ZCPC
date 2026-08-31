package com.example.zcpc.domain.repository

import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.SolvedProblem
import com.example.zcpc.domain.model.Submission
import com.example.zcpc.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface CodeforcesRepository {
    suspend fun getUserProfile(handle: String): NetworkResult<UserProfile>
    suspend fun getContests(): NetworkResult<List<Contest>>

    suspend fun getSolvedProblems(handle: String): NetworkResult<List<SolvedProblem>>
    suspend fun getSubmissions(handle: String): NetworkResult<List<Submission>>
    fun getRivalsFlow(): Flow<List<UserProfile>>
    suspend fun addRival(handle: String): NetworkResult<Unit>
    suspend fun removeRival(profile: UserProfile)
}