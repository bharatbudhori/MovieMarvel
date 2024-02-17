package com.example.moviemarvel.movieList.domain.repository

import com.example.moviemarvel.movieList.domain.model.Movie
import com.example.moviemarvel.movieList.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {
    suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ) : Flow<Resource<List<Movie>>>

    suspend fun getMovie(
        id: Int
    ) : Flow<Resource<Movie>>
}