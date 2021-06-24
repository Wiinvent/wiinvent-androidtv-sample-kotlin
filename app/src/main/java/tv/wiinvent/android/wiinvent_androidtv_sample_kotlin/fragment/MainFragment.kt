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

import java.util.Collections
import java.util.Timer
import java.util.TimerTask

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.leanback.app.BackgroundManager
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.gson.Gson
import okhttp3.*
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.Presenter.CardPresenter
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.Movie
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.MovieList
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.R
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.activity.BrowseErrorActivity
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.activity.DetailsActivity
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.AppConfigRes
import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model.ConfigRes
import java.io.IOException
import java.lang.Integer.parseInt

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseSupportFragment() {

    private val mHandler = Handler()
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null

    private val client: OkHttpClient = OkHttpClient()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        prepareBackgroundManager()

        setupUIElements()

        getConfig("https://wiinvent.tv/config/wiinvent-app-config.json")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + mBackgroundTimer?.toString())
        mBackgroundTimer?.cancel()
    }

    private fun getConfig(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread(Runnable { //Handle UI here
                    Log.e("***** onFailureonFailure", e.toString())
                    loadRows(null)
                    setupEventListeners()
                })
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread(Runnable { //Handle UI here
                    var res = response.body?.string()
                    val gson = Gson()
                    var result = gson.fromJson(res?.trimIndent(), AppConfigRes::class.java)
                    Log.e("***** result", result.toString())
                    loadRows(result)
                    setupEventListeners()
                })
            }
        })
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(activity?.window)
        mDefaultBackground = context?.let { ContextCompat.getDrawable(it,
            R.drawable.default_background
        ) }
        mMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        // over title
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(requireContext(),
            R.color.fastlane_background
        )
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireContext(),
            R.color.search_opaque
        )
    }

    private fun convertConfigToMovie(a: ConfigRes?, id: Int?): Movie {
        var m: Movie = Movie()
        m.id = id
        m.accountId = a!!.accountId
        m.channelId = a!!.channelId
        m.streamId = a!!.streamId
        m.token = a!!.token?.random()
        m.env = a!!.env
        m.contentUrl = a!!.contentUrl
        m.contentType = a!!.contentType
        m.title = a!!.title
        m.description = a!!.description
        m.backgroundImageUrl = a!!.backgroundImageUrl
        m.cardImageUrl = a!!.cardImageUrl
        m.studio = a!!.studio
        return m
    }

    private fun loadRows(config: AppConfigRes?) {
        var listVod =
            MovieList.list
        var listLive =
            MovieList.list
        if (null != config) {
            var lVod:MutableList<Movie>? = ArrayList()
            for (j in 0 until config?.vod?.size!!) {
                var m: Movie = convertConfigToMovie(config?.vod?.get(j), j)
                lVod?.add(m)
            }
            listVod = lVod!!
            var lLivestream:MutableList<Movie>? = ArrayList()
            for (j in 0 until config?.livestream?.size!!) {
                var m: Movie = convertConfigToMovie(config?.livestream?.get(j), j)
                lLivestream?.add(m)
            }
            listLive = lLivestream!!
        }

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()

        for (i in 0 until NUM_ROWS) {
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            if (i == 0) {
                for (j in 0 until listLive.size) {
                    listRowAdapter.add(listLive[j])
                }
            } else {
                for (j in 0 until listVod.size) {
                    listRowAdapter.add(listVod[j])
                }
            }
            val header = HeaderItem(i.toLong(), MovieList.MOVIE_CATEGORY[i])
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(context, "Tìm kiếm... Wiinvent.tv Demo", Toast.LENGTH_LONG)
                .show()
        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is Movie) {
                Log.d(TAG, "Item: " + item.toString())
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                activity!!.startActivity(intent, bundle)
            } else if (item is String) {
                if (item.contains(getString(R.string.error_fragment))) {
                    val intent = Intent(context, BrowseErrorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is Movie) {
                mBackgroundUri = "https://coolbackgrounds.io/images/backgrounds/index/compute-ea4c57a4.png"
//                mBackgroundUri = item.backgroundImageUrl
//                updateBackground(mBackgroundUri)
                startBackgroundTimer()
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(context)
//            .load(uri)
            .load(R.drawable.bg)
            .centerCrop()
            .error(mDefaultBackground)
            .into<SimpleTarget<GlideDrawable>>(
                object : SimpleTarget<GlideDrawable>(width, height) {
                    override fun onResourceReady(
                        resource: GlideDrawable,
                        glideAnimation: GlideAnimation<in GlideDrawable>
                    ) {
                        mBackgroundManager.drawable = resource
                    }
                })
        mBackgroundTimer?.cancel()
    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }

    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(
                GRID_ITEM_WIDTH,
                GRID_ITEM_HEIGHT
            )
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(context!!,
                R.color.default_background
            ))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return Presenter.ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}
    }

    companion object {
        private val TAG = "MainFragment"

        private val BACKGROUND_UPDATE_DELAY = 300
        private val GRID_ITEM_WIDTH = 200
        private val GRID_ITEM_HEIGHT = 200
        private val NUM_ROWS = 2
        private val NUM_COLS = 2
    }
}
