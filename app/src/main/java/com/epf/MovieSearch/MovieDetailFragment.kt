package com.epf.MovieSearch.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epf.MovieSearch.MovieObject
import com.epf.MovieSearch.R
import com.epf.MovieSearch.Service
import com.epf.MovieSearch.movieJsonObject
import com.epf.MovieSearch.ui.favori.FavoriFragment
import com.epf.MovieSearch.ui.recherche.RechercheFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    lateinit var  recyclerView: RecyclerView
    private lateinit var favoritesButton: ImageButton
    private lateinit var overview : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        overview = view.findViewById(R.id.overviewMovieDetail)
        recyclerView = view.findViewById<RecyclerView>(R.id.movieRecoTitle)
        val layoutManager = LinearLayoutManager(requireContext())
       recyclerView.layoutManager = layoutManager
        getMovieDetails(movieID)
        getMovieReco(movieID)
        updateFavoritesButton()
        favoritesButton = view.findViewById(R.id.favoritesButton)
        favoritesButton.setOnClickListener {
            setOnClickFavori(favoritesButton,movieID)
        }
        return view
    }
    private fun setOnClickFavori(button:ImageButton, movieId: Int) {
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
                    // Votre ID de film recherché
                    val movieId = requireArguments().getInt("movieId")
                    val isMovieInFavorites = result?.results?.any { it.id == movieId }

                    if (isMovieInFavorites == true) {

                        // Le film est dans la liste des favoris
                        // Faites quelque chose ici
                        Toast.makeText(context, "Remove Favorie", Toast.LENGTH_LONG).show()
                        removeFavorite(movieId)
                        button.setImageResource(R.drawable.favorite_border)

                    } else {
                        Toast.makeText(context, "add Favorie", Toast.LENGTH_LONG).show()
                        addFavorite(movieId)
                        button.setImageResource(R.drawable.favorite)

                        // Le film n'est pas dans la liste des favoris
                        // Faites autre chose ici

                    }

                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }

    }

    private fun addFavorite(movieId : Int){
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

        val request = Service.AddToFavoritesRequest(
            mediaType = "movie",
            mediaId = movieId,
            favorite = true
        )

        val Service = retrofit.create(Service::class.java)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Service.addToFavorites(19221259," Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiNGU2ZTljM2ViYjcxZWVkMTA3NzM3YmRlZTZhOTAzZSIsInN1YiI6IjY0NGU2MzBlZTFmYWVkMDM4OTAxZDFiNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.m3G_rng54Wm8JApKcb_h5EuEu3r966F5UUt2OpaQ8ys",request)
                }
                if (response.isSuccessful) {
                    Log.e("TAG", "OK : ${response.code()} - ${response.message()}")

                } else {
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }
    private fun removeFavorite(movieId : Int){
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

        val request = Service.AddToFavoritesRequest(
            mediaType = "movie",
            mediaId = movieId,
            favorite = false
        )

        val Service = retrofit.create(Service::class.java)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Service.addToFavorites(19221259," Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiNGU2ZTljM2ViYjcxZWVkMTA3NzM3YmRlZTZhOTAzZSIsInN1YiI6IjY0NGU2MzBlZTFmYWVkMDM4OTAxZDFiNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.m3G_rng54Wm8JApKcb_h5EuEu3r966F5UUt2OpaQ8ys",request)
                }
                if (response.isSuccessful) {
                    Log.e("TAG", "OK : ${response.code()} - ${response.message()}")

                } else {
                    Log.e("TAG","eeeeeeee")
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }
    private fun updateFavoritesButton() {
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
                    // Votre ID de film recherché
                    val movieId = requireArguments().getInt("movieId")

                    // Vérifiez si l'ID du film est dans la liste des résultats
                    val isMovieInFavorites = result?.results?.any { it.id == movieId }

                    if (isMovieInFavorites == true) {

                        // Le film est dans la liste des favoris
                        // Faites quelque chose ici
                        favoritesButton.setImageResource(R.drawable.favorite)
                    } else {

                        // Le film n'est pas dans la liste des favoris
                        // Faites autre chose ici
                        favoritesButton.setImageResource(R.drawable.favorite_border)
                    }

                } else {
                    Log.e("TAG", "ZzZZzzzzzzzzzzzzzzzz")
                    Log.e("TAG", "Request failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error: ${e.message}")
            }
        }
    }

    fun getMovieReco(movieID: Int) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(Service::class.java)
        val result = movieService.getMovieRecommendation(movieID)

        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if(response.isSuccessful) {
                    val result = response.body()
                    val adapter = result?.results?.let { MovieAdapter(it, requireContext()) }
                    adapter?.setOnItemClickListener(object :MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            val fragment = MovieDetailFragment.newInstance(movie.id)

                            // Obtenez le gestionnaire de fragments de l'activité
                            val fragmentManager = requireActivity().supportFragmentManager

                            // Remplacez le fragment actuel par le fragment MovieDetailFragment
                            fragmentManager.beginTransaction()
                                .replace(R.id.movieName, fragment)
                                .addToBackStack(null) // Ajoutez la transaction à la pile de retour arrière
                                .commit()

                            // Gérer le clic de l'élément ici
                            // Par exemple, ouvrir un nouveau fragment avec les détails du film sélectionné
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