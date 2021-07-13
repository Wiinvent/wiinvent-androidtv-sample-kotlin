package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.fragment

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
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.R
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.activity.DetailsActivity
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.Movie
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.MovieList
import tv.wiinvent.wiinventsdk.OverlayManager
import tv.wiinvent.wiinventsdk.interfaces.DefaultOverlayEventListener
import tv.wiinvent.wiinventsdk.interfaces.PlayerChangeListener
import tv.wiinvent.wiinventsdk.models.ConfigData
import tv.wiinvent.wiinventsdk.models.OverlayData


/** Handles video playback with media controls. */
class PlaybackVFragment : Fragment() {

    companion object {
        val TAG = PlaybackVFragment.javaClass.canonicalName
        val SAMPLE_CHANNEL_ID = "41"
        val ACCOUNT_ID = "15"
        val TOKEN = "5008"
        val SAMPLE_STREAM_ID = "44"
        val ENV = OverlayData.Environment.DEV
        val CONTENT_TYPE = OverlayData.ContentType.VOD
    }

    private var exoplayerView: PlayerView? = null
    private var exoplayer: SimpleExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var concatenatingMediaSource: ConcatenatingMediaSource? = null
    private var playbackStateBuilder: PlaybackStateCompat.Builder? = null
    private var overlayManager: OverlayManager? = null

    private var video: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        video = activity?.intent?.getSerializableExtra(DetailsActivity.MOVIE) as Movie
        Log.e("*** tokennnnnn", video!!.token)
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

        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            initializePlayer()
            initializeOverlays()
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
                    PlaybackStateCompat.ACTION_FAST_FORWARD
        )

        mediaSession = MediaSessionCompat(context, "ExoPlayer", componentName, null)
        mediaSession?.setPlaybackState(playbackStateBuilder?.build())
        mediaSession?.isActive = true

        concatenatingMediaSource = ConcatenatingMediaSource()
    }

    private fun initializeOverlays() {
        var url: String? = MovieList.URL_INIT
        val overlayData: OverlayData?
        if (null != video) {
            val env = if ("DEV" == video?.env) {
                OverlayData.Environment.DEV
            } else {
                OverlayData.Environment.PRODUCTION
            }
            val contentType = if ("VOD" == video?.contentType) {
                OverlayData.ContentType.VOD
            } else {
                OverlayData.ContentType.LIVESTREAM
            }
            overlayData = OverlayData.Builder()
                .channelId("" + video?.channelId)
                .accountId("" + video?.accountId)
                .thirdPartyToken("" + video?.token)
                .streamId("" + video?.streamId)
                .debug(true)
                .previewMode(true)
                .env(env)
                .contentType(contentType)
                .deviceType(OverlayData.DeviceType.TV)
                .mappingType(OverlayData.MappingType.WI)
                .build()
            url = video!!.contentUrl
        } else {
            overlayData = OverlayData.Builder()
                .channelId(SAMPLE_CHANNEL_ID)
                .accountId(ACCOUNT_ID)
                .thirdPartyToken(TOKEN)
                .streamId(SAMPLE_STREAM_ID)
                .debug(true)
                .previewMode(true)
                .env(ENV)
                .contentType(CONTENT_TYPE)
                .deviceType(OverlayData.DeviceType.TV)
                .mappingType(OverlayData.MappingType.WI)
                .build()
        }

        overlayManager = OverlayManager(
            requireActivity(),
            R.id.wisdk_overlay_view,
            overlayData!!
        )
        overlayManager?.addOverlayListener(object : DefaultOverlayEventListener {
            override fun onConfigReady(configData: ConfigData) {
                activity?.runOnUiThread {
                    val mediaSource = buildMediaSource(url.toString())
                    concatenatingMediaSource?.addMediaSource(mediaSource)

                    exoplayer?.playWhenReady = true
                    exoplayer?.prepare(concatenatingMediaSource)
                }
            }

            override fun onLoadError() {
                activity?.runOnUiThread {
                    val mediaSource = buildMediaSource(url.toString())
                    concatenatingMediaSource?.addMediaSource(mediaSource)

                    exoplayer?.playWhenReady = true
                    exoplayer?.prepare(concatenatingMediaSource)
                }
            }

            override fun onTimeout() {
                activity?.runOnUiThread {
                    val mediaSource = buildMediaSource(url.toString())
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
        overlayManager?.addPlayerListener(object : PlayerChangeListener {
            override val currentPosition: Long?
                get() = exoplayer?.currentPosition
        })

        // Add player event listeners to determine overlay visibility.
        exoplayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.d(
                    TAG,
                    "**** onPlayerStateChanged playWhenReady: $playWhenReady - $playbackState"
                )
                overlayManager?.setVisible(playWhenReady && playbackState == Player.STATE_READY)

                if (playbackState == PlaybackStateCompat.STATE_FAST_FORWARDING) {
                    //do something
                    Log.e("**** ABC", "PlaybackStateCompat.STATE_FAST_FORWARDING")
                }
                if (playbackState == PlaybackStateCompat.STATE_REWINDING) {
                    //do something else
                    Log.e("**** ABC", "PlaybackStateCompat.STATE_REWINDING")
                }
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                if (error?.type == ExoPlaybackException.TYPE_SOURCE) {
                    playNextMediaSource()
                }
            }
        })
//        exoplayer?.addListener(object : ExoPlayer.EventListener {
//            override fun onPlayerStateChanged(
//                playWhenReady: Boolean,
//                playbackState: Int
//            ) {
//                if (playbackState == PlaybackStateCompat.STATE_FAST_FORWARDING) {
//                    //do something
//                    Log.e("**** ABC", "PlaybackStateCompat.STATE_FAST_FORWARDING")
//                }
//                if (playbackState == PlaybackStateCompat.STATE_REWINDING) {
//                    //do something else
//                    Log.e("**** ABC", "PlaybackStateCompat.STATE_REWINDING")
//                }
//            }
//
//            override fun onPlayerError(error: ExoPlaybackException?) {
//                exoplayer?.stop()
//            }
//        })
    }

    private fun buildMediaSource(url: String): MediaSource {
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

    public fun onEnterBtn() {
        Log.e("**** current play", "" + exoplayer?.playWhenReady!!)
        exoplayer?.playWhenReady = !exoplayer?.playWhenReady!!
    }
}