package com.bohregard.web

import kotlinx.serialization.Serializable

@Serializable
data class KilnRequest(
    val ids: List<String>
)
