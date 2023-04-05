package com.example.walking.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Walking(
    @Expose
    @SerializedName("getWalkingKr")
    val getWalkingKr: GetWalkingKr
)

data class GetWalkingKr(
    @Expose
    @SerializedName("item")
    val item: MutableList<Item>
)

data class Item(
    @Expose
    @SerializedName("MAIN_TITLE")
    val MAIN_TITLE: String,
    @SerializedName("MAIN_IMG_THUMB")
    val MAIN_IMG_THUMB: String,
    @SerializedName("ITEMCNTNTS")
    val ITEMCNTNTS: String,
    @SerializedName("LAT")
    val LAT: String,
    @SerializedName("LNG")
    val LNG: String,
    @SerializedName("UC_SEQ")
    val UC_SEQ: Int




)