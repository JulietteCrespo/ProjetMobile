package com.epf.MovieSearch

import android.text.Editable
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    companion object {


        const val API_KEY = "b4e6e9c3ebb71eed107737bdee6a903e"
        const val ACCOUNT_ID = 19221259
        const val ACCESS_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiNGU2ZTljM2ViYjcxZWVkMTA3NzM3YmRlZTZhOTAzZSIsInN1YiI6IjY0NGU2MzBlZTFmYWVkMDM4OTAxZDFiNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.m3G_rng54Wm8JApKcb_h5EuEu3r966F5UUt2OpaQ8ys"
    }

    @GET("movie?api_key=$API_KEY")
    fun getService(@Query("query") movie: Editable): Call<MovieObject>

    @GET("movie/popular?api_key=$API_KEY")
    fun getPopular(): Call<MovieObject>

    @GET("movie/now_playing?api_key=$API_KEY")
    fun getPlaying(): Call<MovieObject>

    @GET("movie/top_rated?api_key=$API_KEY")
    fun getTop(): Call<MovieObject>

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

    @GET("account/{accountId}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("accountId") accountId: Int, @Header("Authorization") authorization: String
    ): Response<MovieResponse>

    @POST("account/{accountId}/favorite")
    suspend fun addToFavorites(
        @Path("accountId") accountId: Int,
        @Header("Authorization") authorization: String,
        @Body requestBody: AddToFavoritesRequest
    ): Response<AddToFavoritesResponse>


    data class AddToFavoritesRequest(
        @SerializedName("media_type") val mediaType: String,
        @SerializedName("media_id") val mediaId: Int,
        @SerializedName("favorite") val favorite: Boolean
    )

    data class AddToFavoritesResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("status_code") val status_code: Int,
        @SerializedName("status_message") val favorite: String
    )


}