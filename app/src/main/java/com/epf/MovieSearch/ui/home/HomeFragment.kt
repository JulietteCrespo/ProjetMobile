package com.epf.MovieSearch.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epf.MovieSearch.MovieObject
import com.epf.MovieSearch.R
import com.epf.MovieSearch.Service
import com.epf.MovieSearch.movieJsonObject
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

lateinit var overview : TextView
lateinit var  recyclerView: RecyclerView

class HomeFragment : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        overview = rootView.findViewById(R.id.overviewHome)
        recyclerView = rootView.findViewById(R.id.moviePopuTitle)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        getPopularMovie()
        return rootView
    }
    fun getPopularMovie() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(Service::class.java)
        val result = movieService.getPopular()

        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if(response.isSuccessful) {
                    val result = response.body()
                    val adapter = result?.results?.let { MovieAdapter(it, requireContext()) }

                    adapter?.setOnItemClickListener(object : MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            val navController = view?.let { Navigation.findNavController(it) }
                            if (navController != null) {
                                navController.navigate(R.id.navigation_detail_movie, bundleOf("movieId" to movie.id))
                            }
                        }
                    })
                    recyclerView.adapter = adapter
                    overview.text = result?.results?.get(0)?.overview
                }
            }
            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }
        })
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}