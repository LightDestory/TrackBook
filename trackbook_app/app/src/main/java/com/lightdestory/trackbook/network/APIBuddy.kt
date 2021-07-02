package com.lightdestory.trackbook.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class APIBuddy private constructor(context: Context) {

    private var networkQueue: RequestQueue = Volley.newRequestQueue(context)

    companion object {
        private var instance: APIBuddy? = null

        fun getInstance(context: Context): APIBuddy {
            if(instance == null)
                instance = APIBuddy(context.applicationContext)
            return instance!!
        }
    }

    fun addRequest(request: JsonObjectRequest) {
        networkQueue.add(request)
    }

    fun stopPending(tag: String){
        networkQueue.cancelAll(tag)
    }
}