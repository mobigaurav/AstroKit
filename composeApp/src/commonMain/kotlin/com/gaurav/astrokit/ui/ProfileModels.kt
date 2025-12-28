package com.gaurav.astrokit.ui

data class UserProfile(
    val id: String,
    val name: String,
    val dob: Dob
)

fun defaultProfiles(): List<UserProfile> = listOf(
    UserProfile(id = "me", name = "Me", dob = Dob(1990, 10, 25)),
    UserProfile(id = "partner", name = "Partner", dob = Dob(1990, 1, 1)),
    UserProfile(id = "child", name = "Child", dob = Dob(2018, 1, 1))
)
