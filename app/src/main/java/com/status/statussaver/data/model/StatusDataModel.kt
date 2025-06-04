package com.status.statussaver.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class StatusDataModel(
    val filename: String,
    val filepath: String,
    val fileSize: Long = 0,
    val fileFormat: String? = null,
    val creationDate: Long? = null,
    val lastModifiedDate: Long? = null,
    val expiryTime: Long? = null,
) : Parcelable
