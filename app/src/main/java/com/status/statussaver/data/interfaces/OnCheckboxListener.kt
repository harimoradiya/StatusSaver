package com.status.statussaver.data.interfaces

import android.view.View
import com.status.statussaver.data.model.StatusDataModel

interface OnCheckboxListener {
    fun onCheckboxListener(view: View?, list: List<StatusDataModel?>?)
}