package com.example.netflix.model

data class MovieDetail(
    val movie: Movie,
    val similars: List<Movie>
)
