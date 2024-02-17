package com.example.moviemarvel.movieList.data.repository

import com.example.moviemarvel.movieList.data.local.movie.MovieDatabase
import com.example.moviemarvel.movieList.data.mappers.toMovie
import com.example.moviemarvel.movieList.data.mappers.toMovieEntity
import com.example.moviemarvel.movieList.data.remote.MovieApi
import com.example.moviemarvel.movieList.domain.model.Movie
import com.example.moviemarvel.movieList.domain.repository.MovieListRepository
import com.example.moviemarvel.movieList.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieListRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDatabase: MovieDatabase
) : MovieListRepository {
    override suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))

            val localMovieList = movieDatabase.movieDao().getMovieListByCategory(category)

            val shouldLoadLocalMovie = localMovieList.isNotEmpty() && !forceFetchFromRemote

            if (shouldLoadLocalMovie) {
                emit(Resource.Success(data = localMovieList.map { it.toMovie(category) }))
                emit(Resource.Loading(false))
                return@flow
            }

            val movieListFromApi = try {
                movieApi.getMoviesList(category, page)
            } catch (e: Exception) {
                emit(Resource.Error(message = e.message ?: "An error occurred"))
                emit(Resource.Loading(false))
                return@flow
            }

            val movieEntities = movieListFromApi.results.let {
                it.map { movieDto ->
                    movieDto.toMovieEntity(category)
                }
            }

            movieDatabase.movieDao().upsertMovieList(movieEntities)

            emit(Resource.Success(data = movieEntities.map { it.toMovie(category) }))
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getMovie(id: Int): Flow<Resource<Movie>> {
        return flow {
            emit(Resource.Loading(true))

            val localMovie = movieDatabase.movieDao().getMovieById(id)

            if (localMovie != null) {
                emit(Resource.Success(data = localMovie.toMovie(localMovie.category)))
                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Error(message = "Movie not found"))
            emit(Resource.Loading(false))
        }
    }
}