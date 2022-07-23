package com.example.sharedexoplayer

import android.graphics.Bitmap
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize

object PlayerHolder {

    var returnBack = false
    var isPlayer = false
    var player: Player? = null

    var aspectRatio: Float = 0.0F
    var onAspectRatio: ((Float) -> Unit)? = null

    var onFirstRender: (() -> Unit)? = null

    fun startPlayer() {
        if (isPlayer) return
        isPlayer = true
        //val mediaItem = MediaItem.fromUri("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4")
        //val mediaItem = MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4")
        val mediaItem = MediaItem.fromUri("https://v.ozone.ru/vod/video-1/01G30P8HA99AH8638C5DWC3YPJ/asset_2.mp4")
        player?.let {
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
            it.repeatMode = Player.REPEAT_MODE_ALL
            it.addListener(object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    onFirstRender?.invoke()
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    aspectRatio = videoSize.pixelWidthHeightRatio
                    onAspectRatio?.invoke(aspectRatio)
                    super.onVideoSizeChanged(videoSize)
                }

                override fun onRenderedFirstFrame() {
                    onFirstRender?.invoke()
                }
            })
        }
    }
}