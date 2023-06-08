package com.epf.MovieSearch.ui.favori

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epf.MovieSearch.MovieObject
import com.epf.MovieSearch.R
import com.epf.MovieSearch.Service
import com.epf.MovieSearch.Service.Companion.ACCESS_TOKEN
import com.epf.MovieSearch.movieJsonObject
import com.epf.MovieSearch.ui.MovieDetailFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var overview : TextView
lateinit var  recyclerView: RecyclerView
/**
 * A simple [Fragment] subclass.
 * Use the [FavoriFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


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
        val rootView = inflater.inflate(R.layout.fragment_favori, container, false)
        overview = rootView.findViewById(R.id.overviewFavori)
       recyclerView = rootView.findViewById(R.id.movieFavori)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        getFavoriMovie()

        return rootView    }

     fun getFavoriMovie() {
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

         lifecycleScope.launch {
             try {
                 val response = withContext(Dispatchers.IO) {
                     Service.getFavoriteMovies(19221259," Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiNGU2ZTljM2ViYjcxZWVkMTA3NzM3YmRlZTZhOTAzZSIsInN1YiI6IjY0NGU2MzBlZTFmYWVkMDM4OTAxZDFiNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.m3G_rng54Wm8JApKcb_h5EuEu3r966F5UUt2OpaQ8ys")
                 }
                 if (response.isSuccessful) {

                     val result = response.body()
                     val adapter = result?.results?.let {
                         FavoriFragment.MovieAdapter(
                             it,
                             requireContext()
                         )
                     }
                     adapter?.setOnItemClickListener(object : FavoriFragment.MovieAdapter.OnItemClickListener {
                         override fun onItemClick(movie: movieJsonObject) {
                             val navController = view?.let { Navigation.findNavController(it) }
                             if (navController != null) {
                                 navController.navigate(R.id.navigation_detail_movie, bundleOf("movieId" to movie.id))
                             }
                         }
                     })
                     recyclerView.adapter = adapter
                     overview.text = result?.results?.get(0)?.overview


                 } else {
                     Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                 }
             } catch (e: Exception) {
                 Log.e("TAG", "Error: ${e.message}")
             }
         }

    }

    class MovieAdapter(private val movies : Array<movieJsonObject>, val context : Context) : RecyclerView.Adapter<MovieViewHolder>() {
        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(movie: movieJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            return MovieViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            val movie : movieJsonObject = movies[position]
            val view = holder.itemView
            val titreMovie = view.findViewById<TextView>(R.id.movie_title)
            titreMovie.text = movie.original_title

            val dateMovie = view.findViewById<TextView>(R.id.movie_date)
            dateMovie.text = movie.release_date

            val imageMovie = view.findViewById<ImageView>(R.id.movie_imageview)
            val posterUrl = "https://image.tmdb.org/t/p/original" + movie.poster_path
            Picasso.get().load(posterUrl).into(imageMovie);
            view.setOnClickListener {
                itemClickListener?.onItemClick(movie)
            }
            val ratingBar = view.findViewById<RatingBar>(R.id.details_ratingbar_item)
            ratingBar.rating = movie.vote_average?.toFloat()!!
            val textViewAverageRating = view.findViewById<TextView>(R.id.details_average_rating_item)
            textViewAverageRating.text = "("+ "%.1f".format(movie.vote_average).toString()+")"


        }


    }
    class MovieViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_movie, viewGroup, false))

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}