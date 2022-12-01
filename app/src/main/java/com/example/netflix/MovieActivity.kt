package com.example.netflix

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflix.databinding.ActivityMovieBinding
import com.example.netflix.model.Movie
import com.example.netflix.model.MovieDetail
import com.example.netflix.util.DownloadImageTask
import com.example.netflix.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.Callback {
    private lateinit var binding: ActivityMovieBinding
    private lateinit var txtTitle: TextView
    private lateinit var txtDesc: TextView
    private lateinit var txtCast: TextView
    private lateinit var rvSimilar: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: MovieAdapter

    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        txtTitle = binding.txtMovieTitle
        txtDesc = binding.txtMovieDesc
        txtCast = binding.txtCast
        rvSimilar = binding.rvMoviesSimilar
        progress = binding.progressMovieSimilar

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID não encontrado!")

        val url =
            "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=91a29451-1dad-419a-81a2-f0c3ceb75603"

        MovieTask(this).execute(url)

        rvSimilar.layoutManager = GridLayoutManager(this, 3)
        adapter = MovieAdapter(movies, R.layout.item_movie_similar)
        rvSimilar.adapter = adapter

        val toolbar: Toolbar = binding.tbMovie
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onResult(movieDetail: MovieDetail) {
        progress.visibility = View.GONE

        txtTitle.text = movieDetail.movie.title
        txtDesc.text = movieDetail.movie.desc
        txtCast.text = movieDetail.movie.cast

        movies.clear()
        movies.addAll(movieDetail.similars)
        adapter.notifyDataSetChanged()

        DownloadImageTask(object : DownloadImageTask.Callback {
            override fun onResult(bitmap: Bitmap) {
                val layerDrawable: LayerDrawable =
                    ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
                val movieCover = BitmapDrawable(resources, bitmap)
                layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
                val coverImg: ImageView = findViewById(R.id.img_movie)
                coverImg.setImageDrawable(layerDrawable)
            }
        }).execute(movieDetail.movie.coverUrl)
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
