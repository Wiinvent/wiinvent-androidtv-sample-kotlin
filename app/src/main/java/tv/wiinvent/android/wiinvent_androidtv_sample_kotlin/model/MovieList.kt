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

package tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.model

import tv.wiinvent.android.wiinvent_androidtv_sample_kotlin.R

object MovieList {
    val MOVIE_CATEGORY = arrayOf(
        "K+",
        "VTV1"
    )

    val list: List<Movie> by lazy {
        setupMovies()
    }
    private var count: Long = 0

    private fun setupMovies(): List<Movie> {
        val title = arrayOf(
            "Giải trí tương tác",
            "PlayIQ"
        )

        val descriptions = arrayOf(
            "Gói Nội Dung Giải Trí Tương Tác là chuyên mục bao gồm các nội dung Mini-gameshow giải trí ấn tượng kết hợp tương tác trực tiếp. Chỉ với một chạm, khách hàng có thể trực tiếp tham gia vào gameshow từ nội dung, đến việc tương tác trực tiếp với khách mời hoặc streamer.",
            "Chương trình sẽ được phủ trên tất cả các sóng OTT (từ VODs đến livestream), là hình thức TƯƠNG TÁC và TẶNG QUÀ TRỰC TIẾP cho members, từng bước xây dựng nền tảng WATCH AND EARN."
        )
        val studio = arrayOf(
            "Wiinvent.tv",
            "V-Entertaiment"
        )
        val videoUrl = arrayOf(
            "https://dev.wiinvent.tv/videos/votingdemo.mp4",
            "https://dev.wiinvent.tv/videos/donationdemo.mp4"
        )
        val bgImageUrl = arrayOf(
            "https://static.independent.co.uk/s3fs-public/thumbnails/image/2014/07/08/22/v2david-luiz.jpg?w968",
            "https://www.doctorswithoutborders.org/sites/default/files/styles/crop_7x3_full_width_hero/public/image_base_media/2018/06/MSF223938.jpg?h=c64a258d&itok=Lvy6XZGx"
        )
        val cardImageUrl = arrayOf(
                R.drawable.gttt,
                R.drawable.play
        )

        val list = title.indices.map {
            buildMovieInfo(
                title[it],
                descriptions[it],
                studio[it],
                videoUrl[it],
                "" + cardImageUrl[it],
                bgImageUrl[it]
            )
        }

        return list
    }

    private fun buildMovieInfo(
        title: String,
        description: String,
        studio: String,
        videoUrl: String,
        cardImageUrl: String,
        backgroundImageUrl: String
    ): Movie {
        val movie =
            Movie()
        movie.id = count++
        movie.title = title
        movie.description = description
        movie.studio = studio
        movie.cardImageUrl = cardImageUrl
        movie.backgroundImageUrl = backgroundImageUrl
        movie.videoUrl = videoUrl
        return movie
    }
}