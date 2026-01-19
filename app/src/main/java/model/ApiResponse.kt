package com.xirpl2.SASMobile.model

data class ApiResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)