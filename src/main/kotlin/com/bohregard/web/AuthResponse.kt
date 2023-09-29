package com.bohregard.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("kiln_info") val kilnInfo: List<KilnInfo>,
    val token: String,
)

@Serializable
data class KilnInfo(
    val id: String,
    val name: String
)