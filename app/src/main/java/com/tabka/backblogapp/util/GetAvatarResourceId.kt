package com.tabka.backblogapp.util

import com.tabka.backblogapp.R

fun getAvatarResourceId(avatarId: Int): Int {
    return when (avatarId) {
        1 -> R.drawable.avatar1
        2 -> R.drawable.avatar2
        3 -> R.drawable.avatar3
        else -> R.drawable.avatar1 // Default fallback
    }
}