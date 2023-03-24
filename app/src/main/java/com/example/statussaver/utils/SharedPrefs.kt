package com.example.statussaver.utils

import android.content.Context
import android.content.SharedPreferences




object  SharedPrefs {
    val WA_TREE_URI = "wa_tree_uri"
    val WB_TREE_URI = "wb_tree_uri"
    private var mPreferences: SharedPreferences? = null


    private fun getInstance(context: Context): SharedPreferences? {
        if (mPreferences == null) {
            mPreferences = context.applicationContext.getSharedPreferences("wa_data", 0)
        }
        return mPreferences
    }


    fun setWATree(context: Context?, value: String?) {
        getInstance(context!!)!!.edit().putString(WA_TREE_URI, value).apply()
    }

    fun getWATree(context: Context?): String? {
        return getInstance(context!!)!!.getString(WA_TREE_URI, "")
    }

    fun setWBTree(context: Context?, value: String?) {
        getInstance(context!!)!!.edit().putString(WB_TREE_URI, value).apply()
    }

    fun getWBTree(context: Context?): String? {
        return getInstance(context!!)!!.getString(WB_TREE_URI, "")
    }




}