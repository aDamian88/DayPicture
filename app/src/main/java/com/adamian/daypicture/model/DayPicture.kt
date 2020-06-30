package com.adamian.daypicture.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DayPicture(

    @Expose
    @SerializedName("title")
    val title: String? = null,

    @Expose
    @SerializedName("explanation")
    val explanation: String? = null,

    @Expose
    @SerializedName("url")
    val image: String? = null
) {
    override fun toString(): String {
        return "DayPicture(title=$title, explanation=$explanation, image=$image)"
    }
}