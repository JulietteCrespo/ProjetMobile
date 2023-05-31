package com.epf.MovieSearch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var searchMovie : EditText
    lateinit var searchMovieButton : Button
    lateinit var nameMovie : RecyclerView
    lateinit var overview : TextView
    lateinit var  recyclerView: RecyclerView



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchMovie = findViewById(R.id.searchMovie)
        searchMovieButton = findViewById(R.id.search)
        overview = findViewById(R.id.overview)

        searchMovieButton.setOnClickListener {
            val titleMovie = searchMovie.text
            if(titleMovie.isEmpty()) {
                Toast.makeText(applicationContext, "Le champ recherche est vide", Toast.LENGTH_LONG).show()
            } else {
                getToSearchMovie(titleMovie)

            }
        }
    }
     fun getToSearchMovie(titleMovie: Editable) {

         val retrofit: Retrofit = Retrofit.Builder()
             .baseUrl("https://api.themoviedb.org/3/search/")
             .addConverterFactory(GsonConverterFactory.create())
             .build()


         this.recyclerView = findViewById(R.id.movieTitle)
         recyclerView.layoutManager = GridLayoutManager(this, GridLayoutManager.VERTICAL)
         val movieService = retrofit.create(Service::class.java)
         val result = movieService.getService(titleMovie)


         result.enqueue(object : Callback<MovieObject> {
             override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                 if(response.isSuccessful) {
                     var result = response.body()
                     val adapter = result?.results?.let { MovieAdapter(it, this@MainActivity) }
                     recyclerView.adapter = adapter
                     overview.text = result?.results?.get(0)?.overview

                 }
             }

             override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                 Toast.makeText(applicationContext, "error server", Toast.LENGTH_LONG).show()
             }


         })
     }

    class MovieAdapter(private val movies : Array<movieJsonObject>, val context : Context) : RecyclerView.Adapter<MovieViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            return MovieViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            val movie : movieJsonObject = movies[position]
            val view = holder.itemView
            val textView = view.findViewById<TextView>(R.id.movieItem)
            textView.text = movie.original_title

        }


    }
    class MovieViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_movie, viewGroup, false))



}

