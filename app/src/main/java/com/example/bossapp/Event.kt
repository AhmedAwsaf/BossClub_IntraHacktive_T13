package com.example.bossapp

data class Event(
    val eventName: String = "",
    val eventDescription: String = "",
    val eventStartDate: String = "",
    val eventEndDate: String = "",
    val eventFeatures: List<String> = emptyList(),
    val status: String = "waiting",
    val signedBy: String = "NONE",
    val club: String = "",
    val addedBy : String = ""
)

