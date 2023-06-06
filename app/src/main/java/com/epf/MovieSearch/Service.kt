package com.epf.MovieSearch

import android.text.Editable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
companion object {
    const val API_KEY = "b4e6e9c3ebb71eed107737bdee6a903e"
    const val ACCOUNT_ID = 19221259
    const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiNGU2ZTljM2ViYjcxZWVkMTA3NzM3YmRlZTZhOTAzZSIsInN1YiI6IjY0NGU2MzBlZTFmYWVkMDM4OTAxZDFiNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.m3G_rng54Wm8JApKcb_h5EuEu3r966F5UUt2OpaQ8ys"
}

    @GET("movie?api_key=$API_KEY")
    fun getService(@Query("query") movie: Editable): Call<MovieObject>

    @GET("movie/{movie_id}?api_key=$API_KEY")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String,
        @Query("append_to_response") appendToResponse: String
    ): Response<movieJsonObject>

    @GET("movie/{movie_id}/recommendations?api_key=$API_KEY")
     fun getMovieRecommendation(
        @Path("movie_id") movieId: Int,
    ): Call<MovieObject>

    @GET ("account/{accountId}/favorite/movies")
    suspend fun getFavoriteMovies (
        @Path("accountId") accountId: Int, @Header ("Authorization") authorization: String
    ) : Response<MovieResponse>
}

