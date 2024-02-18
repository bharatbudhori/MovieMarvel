package com.example.moviemarvel.di

import android.app.Application
import androidx.room.Room
import com.example.moviemarvel.movieList.data.local.movie.MovieDatabase
import com.example.moviemarvel.movieList.data.remote.MovieApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val interceptor: Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Singleton
    @Provides
    fun providesMovieApi(): MovieApi {
        return Retrofit.Builder()
            .baseUrl(MovieApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MovieApi::class.java)
    }

    @Singleton
    @Provides
    fun providesMovieDatabase(app: Application): MovieDatabase {
        return Room.databaseBuilder(
            app, MovieDatabase::class.java,
            "moviedb.db"
        ).build()
    }
}