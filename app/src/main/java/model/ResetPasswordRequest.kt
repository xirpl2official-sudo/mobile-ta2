package com.xirpl2.SASMobile.model
data class ResetPasswordRequest(
    val nis: String,
    val email: String,
    val otp: String,
    val newPassword: String
)