package com.example.moviemarvel.details.presentation

import com.example.moviemarvel.movieList.domain.model.Movie

data class DetailsState(
    val isLoading: Boolean = false,
    val movie: Movie? = null
)
