package com.example.zcpc.domain.repository

import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.SolvedProblem
import com.example.zcpc.domain.model.UserProfile

interface CodeforcesRepository {
    suspend fun getUserProfile(handle: String): NetworkResult<UserProfile>
    suspend fun getContests(): NetworkResult<List<Contest>>

    suspend fun getSolvedProblems(handle: String): NetworkResult<List<SolvedProblem>>
}