package com.xirpl2.SASMobile.model

data class OtpVerificationRequest(
    val nis: String,
    val email: String,
    val otp: String
)