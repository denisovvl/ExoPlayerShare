package com.example.sharedexoplayer

import com.google.android.exoplayer2.ui.PlayerView

interface PlayerPositionProvider {

    fun getPlayer(): PlayerView

    fun getPlayerPosition(): PlayerPosition
}