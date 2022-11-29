package com.example.netflix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflix.databinding.ActivityMainBinding
import com.example.netflix.model.Category
import com.example.netflix.model.Movie

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val categories = mutableListOf<Category>()
        for (j in 0 until 10) {

            val movies = mutableListOf<Movie>()
            for (i in 0 until 15) {
                val movie = Movie(R.drawable.movie)
                movies.add(movie)
            }

            val category = Category("cat $j", movies)
            categories.add(category)
        }

        val adapter = CategoryAdapter(categories)
        val rv = binding.rvMain
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }
}