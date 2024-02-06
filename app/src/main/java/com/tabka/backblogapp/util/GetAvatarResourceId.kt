package com.tabka.backblogapp.util

import com.tabka.backblogapp.R

fun getAvatarResourceId(avatarId: Int): Pair<String, Int> {
    return when (avatarId) {
        1 -> Pair("Quasar", R.drawable.avatar1)
        2 -> Pair("Cipher", R.drawable.avatar2)
        3 -> Pair("Nova", R.drawable.avatar3)
        4 -> Pair("Flux", R.drawable.avatar4)
        5 -> Pair("Torrent", R.drawable.avatar5)
        6 -> Pair("Odyssey", R.drawable.avatar6)
        else -> Pair("Quasar", R.drawable.avatar1) // Default
    }
}