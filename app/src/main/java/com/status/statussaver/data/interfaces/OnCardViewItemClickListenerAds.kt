package com.status.statussaver.data.interfaces

import android.view.View
import com.status.statussaver.data.model.StatusDataModel
import java.text.FieldPosition

interface OnCardViewItemClickListenerAds {
    fun onCardViewListener(mData: ArrayList<StatusDataModel>, position: Int, isWapp:Boolean)
}