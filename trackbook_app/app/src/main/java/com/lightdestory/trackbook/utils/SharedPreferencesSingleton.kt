package com.lightdestory.trackbook.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.lightdestory.trackbook.R


class SharedPreferencesSingleton constructor(context: Context) {

    val preferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)

    companion object {
        private var instance: SharedPreferencesSingleton? = null

        fun getInstance(context: Context?): SharedPreferencesSingleton {
            if (instance == null)
                if (context != null) {
                    instance = SharedPreferencesSingleton(context.applicationContext)
                }
            return instance!!
        }
    }
}