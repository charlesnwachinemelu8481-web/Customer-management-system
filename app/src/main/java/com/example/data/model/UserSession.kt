package com.example.data.model

data class UserSession(
    val email: String,
    val fullName: String,
    val company: String,
    val role: String,
    val workspaceUrl: String,
    val isLoggedIn: Boolean = false
) {
    companion object {
        val DemoUser = UserSession(
            email = "alex.turner@acmeworks.io",
            fullName = "Alex Turner",
            company = "Acme Technologies HQ",
            role = "Head of Business Development",
            workspaceUrl = "acme.clientbase.io",
            isLoggedIn = true
        )
    }
}
