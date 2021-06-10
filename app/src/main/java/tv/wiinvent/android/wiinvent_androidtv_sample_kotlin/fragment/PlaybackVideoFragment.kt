/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.fragment

import android.app.Activity
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import okhttp3.*
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.R
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.activity.DetailsActivity
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.ConfigRes
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.Movie
import tv.wiinvent.wiinventsdk.OverlayManager
import tv.wiinvent.wiinventsdk.interfaces.DefaultOverlayEventListener
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener
import tv.wiinvent.wiinventsdk.models.ConfigData
import tv.wiinvent.wiinventsdk.models.OverlayData
import java.io.IOException
import java.net.URL
import kotlin.math.log


/** Handles video playback with media controls. */
class PlaybackVideoFragment : Fragment() {

    companion object {
        val TAG = PlaybackVideoFragment.javaClass.canonicalName
        val SAMPLE_CHANNEL_ID = "41"
        val ACCOUNT_ID = "15"
        val TOKEN = "5008"
        val SAMPLE_STREAM_ID = "44"
        val ENV = OverlayData.Environment.DEV
    }

    private var exoplayerView: PlayerView? = null
    private var exoplayer: SimpleExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var concatenatingMediaSource: ConcatenatingMediaSource? = null
    private var playbackStateBuilder: PlaybackStateCompat.Builder? = null
    private var overlayManager: OverlayManager? = null

    private var streamUrl: String? = ""
    private val client: OkHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (_, title, description, _, _, videoUrl) =
            activity?.intent?.getSerializableExtra(DetailsActivity.MOVIE) as Movie
        streamUrl = videoUrl
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playback_video_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoplayerView = activity?.findViewById(R.id.simple_exo_player_view)

        init(savedInstanceState, null)
//        getConfig("https://wiinvent.tv/config/wiinvent-tv-config.json", savedInstanceState)
    }

    private fun getConfig(url: String, savedInstanceState: Bundle?) {
        val request = Request.Builder()
                .url(url)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread(Runnable { //Handle UI here
                    init(savedInstanceState, null)
                })
            }
            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread(Runnable { //Handle UI here
                    var res = response.body?.string()
                    val gson = Gson()
                    var result = gson.fromJson(res?.trimIndent(), ConfigRes::class.java)
                    Log.e("***** result", result.toString())
                    init(savedInstanceState, result)
                })
            }
        })
    }

    private fun init(savedInstanceState: Bundle?, config: ConfigRes?) {
        if (savedInstanceState == null) {
            initializePlayer()
            initializeOverlays(config)
        }
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector()
        val componentName = ComponentName(requireContext(), "Exo")

        exoplayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        exoplayerView?.player = exoplayer
        exoplayerView?.useController = true

        playbackStateBuilder = PlaybackStateCompat.Builder()
        playbackStateBuilder?.setActions(
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_FAST_FORWARD)

        mediaSession = MediaSessionCompat(context, "ExoPlayer", componentName, null)
        mediaSession?.setPlaybackState(playbackStateBuilder?.build())
        mediaSession?.isActive = true

        concatenatingMediaSource = ConcatenatingMediaSource()
    }

    private fun initializeOverlays(config: ConfigRes?) {
        var overlayData: OverlayData? = null
        if (null == config) {
            overlayData = OverlayData.Builder()
                    .channelId(SAMPLE_CHANNEL_ID)
                    .accountId(ACCOUNT_ID)
                    .thirdPartyToken(TOKEN)
                    .streamId(SAMPLE_STREAM_ID)
                    .debug(true)
                    .previewMode(true)
                    .env(ENV)
                    .deviceType(OverlayData.DeviceType.TV)
                    .mappingType(OverlayData.MappingType.WI)
                    .build()
        } else {
            overlayData = OverlayData.Builder()
                    .channelId("" + config.channelId)
                    .accountId("" + config.accountId)
                    .thirdPartyToken(config.token.toString())
                    .streamId("" + config.streamId)
                    .debug(true)
                    .previewMode(true)
                    .env(ENV)
                    .deviceType(OverlayData.DeviceType.TV)
                    .mappingType(OverlayData.MappingType.WI)
                    .build()
        }

        overlayManager = OverlayManager(
            requireActivity(),
            R.id.wisdk_overlay_view,
            overlayData
        )
        overlayManager?.addOverlayListener(object: DefaultOverlayEventListener {
            override fun onConfigReady(configData: ConfigData) {
                activity?.runOnUiThread {
                    var url: String = "https://static1.dev.wiinvent.tv/video/video_url_1623213495784.mp4"
                    if (null != config) {
                        url = config.contentUrl.toString()
                    }
                    val mediaSource = buildMediaSource(url)
                    concatenatingMediaSource?.addMediaSource(mediaSource)

                    exoplayer?.playWhenReady = true
                    exoplayer?.prepare(concatenatingMediaSource)
                }
            }

            override fun onLoadError() {
                activity?.runOnUiThread {
                    var url: String = "https://static1.dev.wiinvent.tv/video/video_url_1623213495784.mp4"
                    if (null != config) {
                        url = config.contentUrl.toString()
                    }
                    val mediaSource = buildMediaSource(url)
                    concatenatingMediaSource?.addMediaSource(mediaSource)

                    exoplayer?.playWhenReady = true
                    exoplayer?.prepare(concatenatingMediaSource)
                }
            }

            override fun onTimeout() {
                activity?.runOnUiThread {
                    var url: String = "https://static1.dev.wiinvent.tv/video/video_url_1623213495784.mp4"
                    if (null != config) {
                        url = config.contentUrl.toString()
                    }
                    val mediaSource = buildMediaSource(url)
                    concatenatingMediaSource?.addMediaSource(mediaSource)

                    exoplayer?.playWhenReady = true
                    exoplayer?.prepare(concatenatingMediaSource)
                }
            }

            override fun onWebViewBrowserClose() {

            }

            override fun onWebViewBrowserContentVisible(isVisible: Boolean) {

            }

            override fun onWebViewBrowserOpen() {

            }
        })

        // Set the player position for VOD playback.
        overlayManager?.addPlayerListener(object: PlayerChangeListener {
            override val currentPosition: Long?
                get() = exoplayer?.currentPosition
        })

        // Add player event listeners to determine overlay visibility.
        exoplayer?.addListener(object : Player.EventListener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.d(TAG, "====onPlayerStateChanged playWhenReady: $playWhenReady - $playbackState")
                overlayManager?.setVisible(playWhenReady && playbackState == Player.STATE_READY)
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                if(error?.type == ExoPlaybackException.TYPE_SOURCE) {
                    playNextMediaSource()
                }
            }

        })
    }

    private fun buildMediaSource(url: String) : MediaSource {
        val userAgent = Util.getUserAgent(context, "Exo")
        val dataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        val uri = Uri.parse(url)

        return when (val type = Util.inferContentType(uri)) {
            C.TYPE_DASH -> DashMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource
                .Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ExtractorMediaSource
                .Factory(dataSourceFactory)
                .setExtractorsFactory(DefaultExtractorsFactory())
                .createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type :: $type")
        }

    }

    private fun playNextMediaSource() {
        // Play next media source by removing current from collection.
        exoplayer?.currentWindowIndex?.let {
            concatenatingMediaSource?.removeMediaSource(it)
        }
        concatenatingMediaSource?.let {
            exoplayer?.playWhenReady = true
            exoplayer?.prepare(concatenatingMediaSource, true, true)
        }
    }

    private fun releasePlayer() {
        if (exoplayer != null) {
            exoplayer?.stop()
            exoplayer?.release()
            exoplayer = null
        }
    }

    private fun releaseOverlays() {
        if (overlayManager != null) {
            overlayManager?.release()
            overlayManager = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        releaseOverlays()
    }

    override fun onPause() {
        super.onPause()
        exoplayer?.playWhenReady = false
    }
}