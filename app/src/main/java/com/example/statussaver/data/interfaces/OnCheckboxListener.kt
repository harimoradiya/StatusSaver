package com.example.statussaver.data.interfaces

import android.view.View
import com.example.statussaver.data.model.StatusDataModel

interface OnCheckboxListener {
    fun onCheckboxListener(view: View?, list: List<StatusDataModel?>?)
}