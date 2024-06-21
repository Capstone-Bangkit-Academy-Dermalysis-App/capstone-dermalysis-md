package com.dermalisys.data.pref

data class UserModel(
    val email: String,
    val name: String,
    val userId: String,
    val oneTapLogin: String,
    val isLogin: Boolean = false
)
