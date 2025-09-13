package com.jkuhldev.openhand.ui.tab

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.compose.PlayerSurface
import com.jkuhldev.openhand.data.PREVIEW_PRINTER
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.network.createSocketFactory
import com.jkuhldev.openhand.ui.screen.PrinterDetailScreen
import com.jkuhldev.openhand.ui.theme.OpenHandTheme

/**
 * Tab for viewing the printer's camera feed
 * @param printer Printer that is being interacted with
 * @param isPreview Boolean that denotes if the Composable is being rendered in a preview or not
 */
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveViewTab(printer: Printer, isPreview: Boolean = LocalInspectionMode.current) {
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().aspectRatio(16f / 9f)
    ) {
        if (isPreview) {
            Surface(
                color = Color.Black,
                modifier = Modifier.fillMaxSize()
            ) {}
            CircularProgressIndicator()
            return@Box
        }

        StreamPlayer(
            context = context,
            mediaSource = RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)
                .setSocketFactory(createSocketFactory(context))
                .createMediaSource(MediaItem.fromUri("rtsps://bblp:${printer.accessCode}@${printer.ipAddress}:322/streaming/live/1"))
        )
    }
}

/**
 * Composable for playing a video stream using ExoPlayer
 * @param context Android context used for initializing the player
 * @param mediaSource MediaSource object that specifies the video stream source
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun StreamPlayer(context: Context, mediaSource: MediaSource) {
    val exoPlayer = remember(mediaSource) { ExoPlayer.Builder(context).build() }
    var isReady by remember(exoPlayer) { mutableStateOf(false) }
    val listener = remember(exoPlayer) {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && !isReady) {
                    isReady = true
                    exoPlayer.removeListener(this)
                }
            }
        }
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.addListener(listener)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    PlayerSurface(player = exoPlayer)
    if (!isReady) {
        CircularProgressIndicator()
    }
}

@PreviewLightDark()
@Composable
private fun LiveViewTabPreview() {
    OpenHandTheme {
        PrinterDetailScreen(
            printer = PREVIEW_PRINTER,
            startTab = 1,
        )
    }
}
