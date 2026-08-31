package com.example.zcpc.data.mapper

import com.example.zcpc.data.codeforces.remote.dto.SubmissionDto
import com.example.zcpc.domain.model.Submission

fun SubmissionDto.toDomain(): Submission {
    return Submission(
        id = this.id,
        contestId = this.problem.contestId,
        index = this.problem.index,
        name = this.problem.name,
        rating = this.problem.rating ?: 0,
        verdict = this.verdict,
        tags = this.problem.tags
    )
}
