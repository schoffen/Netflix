package com.example.netflix

import android.content.Context
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.netflix.databinding.ActivityMovieBinding
import com.example.netflix.model.Movie

class MovieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val txtTitle = binding.txtMovieTitle
        val txtDesc = binding.txtMovieDesc
        val txtCast = binding.txtCast
        val rvSimilar = binding.rvMoviesSimilar

        txtTitle.text = "Batman Begins"
        txtDesc.text = "Essa é a descrição do filme do Batman"
        txtCast.text = getString(R.string.cast, "Ator A, Ator B, Atriz C, Atriz D")

        val movies = mutableListOf<Movie>()

        rvSimilar.layoutManager = GridLayoutManager(this, 3)
        rvSimilar.adapter = MovieAdapter(movies, R.layout.item_movie_similar)

        val toolbar: Toolbar = binding.tbMovie
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this, R.drawable.shadows) as LayerDrawable
        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4)
        layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
        val coverImg: ImageView = findViewById(R.id.img_movie)
        coverImg.setImageDrawable(layerDrawable)
    }
}
