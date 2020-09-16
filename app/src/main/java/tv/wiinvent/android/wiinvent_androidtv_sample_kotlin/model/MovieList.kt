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

object MovieList {
    val MOVIE_CATEGORY = arrayOf(
        "K+1",
        "VTV1"
    )

    val list: List<Movie> by lazy {
        setupMovies()
    }
    private var count: Long = 0

    private fun setupMovies(): List<Movie> {
        val title = arrayOf(
            "Voting",
            "Banner"
        )

        val description = "Fusce id nisi turpis. Praesent viverra bibendum semper. " +
                "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est " +
                "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit " +
                "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit " +
                "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id " +
                "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat."
        val studio = arrayOf(
            "Studio Zero",
            "Studio One"
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
            "https://static.independent.co.uk/s3fs-public/thumbnails/image/2014/07/08/22/brazil5.jpg?width=1368&height=912&fit=bounds&format=pjpg&auto=webp&quality=70",
            "https://www.dw.com/image/52720704_303.jpg"
        )

        val list = title.indices.map {
            buildMovieInfo(
                title[it],
                description,
                studio[it],
                videoUrl[it],
                cardImageUrl[it],
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