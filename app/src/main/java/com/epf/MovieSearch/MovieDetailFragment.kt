package com.epf.MovieSearch.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.epf.MovieSearch.R
import com.epf.MovieSearch.Service
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovieDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var textViewTitle: TextView
    private lateinit var textViewGenre: TextView
    private lateinit var textDetailLanguage: TextView
    private lateinit var textViewRuntime: TextView
    private lateinit var textViewReleaseDate: TextView
    private lateinit var textViewAverageRating: TextView
    private lateinit var textViewOverview: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_detail, container, false)

        // Initiate the views
        textViewTitle = view.findViewById<TextView>(R.id.details_movie_title)
        textDetailLanguage = view.findViewById<TextView>(R.id.details_language)
        textViewGenre = view.findViewById<TextView>(R.id.details_genre)
        textViewRuntime = view.findViewById<TextView>(R.id.details_runtime)
        textViewReleaseDate = view.findViewById<TextView>(R.id.details_release_date)
        textViewAverageRating = view.findViewById<TextView>(R.id.details_average_rating)
        textViewOverview = view.findViewById<TextView>(R.id.details_overview_body)
        ratingBar = view.findViewById<RatingBar>(R.id.details_ratingbar)
        image = view.findViewById<ImageView>(R.id.details_imageview)

        val movieID = requireArguments().getInt("movieId")

        getMovieDetails(movieID)

        return view
        // Inflate the layout for this fragment
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MovieDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun newInstance(movieId: Int): MovieDetailFragment {
            val fragment = MovieDetailFragment()
            val args = Bundle()
            args.putInt("movieId", movieId)
            fragment.arguments = args
            return fragment
        }
    }


    fun getMovieDetails(movieID: Int) {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val Service = retrofit.create(Service::class.java)

        //val movieID = requireArguments().getInt("movieId")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Service.getMovieDetails(movieID,  "en-FR", "credits")
                }
                Log.e("TAG", "Teste  ${response} ")
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    textViewTitle.text = movieDetails?.title
                    textViewRuntime.text = "${movieDetails?.runtime} min"
                    textViewOverview.text = movieDetails?.overview
                    textViewReleaseDate?.text = movieDetails?.release_date?.substring(0, 4)
                    textDetailLanguage.text = "(" + movieDetails?.original_language +")"

                    val genreNames = movieDetails?.genres?.map { it.name } // Récupérer les noms des genres
                    val genreString = genreNames?.joinToString(", ") // Convertir la liste de noms en une chaîne de caractères séparée par des virgules

                    textViewGenre.text = genreString



                    ratingBar.rating = movieDetails?.vote_average?.toFloat()!!
                    textViewAverageRating.text = "("+ "%.1f".format(movieDetails?.vote_average).toString()+")"

                    val imageUrl = "https://image.tmdb.org/t/p/original" + movieDetails?.poster_path
                    Picasso.get().load(imageUrl).into(image);



                } else {
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }
}