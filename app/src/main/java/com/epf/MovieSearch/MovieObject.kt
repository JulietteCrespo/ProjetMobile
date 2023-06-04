package com.epf.MovieSearch

import android.media.tv.TvContract
import android.media.tv.TvContract.Programs.Genres
import com.google.gson.JsonObject

data class MovieObject(
    var page : Int,
    var results : Array<movieJsonObject>
)
data class Genre(
    val id: Int,
    val name: String
)

data class movieJsonObject(
    val adult: Boolean,
    val backdrop_path: String,
    val belongs_to_collection : Any?,
    val budget: Int,
    val genre_ids: List<Int>,
    val genres: List<Genre>,
    val homepage : String,
    val id: Int,
    val imdb_id : String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val revenue : Int,
    val runtime : Int,
    val status : String,
    val tagline : String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)


