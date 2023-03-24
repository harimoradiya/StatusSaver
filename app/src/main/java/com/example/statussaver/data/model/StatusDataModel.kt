package com.example.statussaver.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StatusDataModel(
    var filename: String,
    var filepath: String

) : Parcelable
