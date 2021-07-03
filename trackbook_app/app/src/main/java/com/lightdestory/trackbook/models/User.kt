package com.lightdestory.trackbook.models

data class User(val penName: String, val token: String) {
    companion object {
        lateinit var instance: User

        fun setUser(instance: User) {
            this.instance = instance
        }
    }
}