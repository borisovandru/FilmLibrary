package com.android.filmlibrary.model.repository.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.android.filmlibrary.Constant.IMDB_NAMES_API_KEY
import com.android.filmlibrary.Constant.IMDB_NAMES_API_VERSION
import com.android.filmlibrary.Constant.IMDB_NAMES_LANG
import com.android.filmlibrary.Constant.URL_GENRES_PATH
import com.android.filmlibrary.model.retrofit.ConfigurationAPI
import com.android.filmlibrary.model.retrofit.GenresAPI
import com.android.filmlibrary.model.retrofit.MovieAdvAPI
import com.android.filmlibrary.model.retrofit.MoviesListAPI

interface TheMovieDBAPI {

    @GET("{api_version}/movie/{movieId}")
    fun getMovie(
        @Path(IMDB_NAMES_API_VERSION) apiVersion: String,
        @Path("movieId") standardList: Int,
        @Query(IMDB_NAMES_API_KEY) key: String,
        @Query(IMDB_NAMES_LANG) language: String
    ): Call<MovieAdvAPI>

    @GET("{api_version}/{path_1}/{path_2}/{path_3}")
    fun getGenres(
        @Path("path_1") queryType1: String,
        @Path("path_2") queryType2: String,
        @Path("path_3") queryType3: String,
        @Path(IMDB_NAMES_API_VERSION) apiVersion: String,
        @Query(IMDB_NAMES_API_KEY) key: String,
        @Query(IMDB_NAMES_LANG) language: String,
    ): Call<GenresAPI>

    @GET("{api_version}/{path_1}/{path_2}")
    fun getMoviesByGenre(
        @Path("path_1") queryType1: String,
        @Path("path_2") queryType2: String,
        @Path(IMDB_NAMES_API_VERSION) apiVersion: String,
        @Query(IMDB_NAMES_API_KEY) key: String,
        @Query(IMDB_NAMES_LANG) language: String,
        @Query(URL_GENRES_PATH) genreId: Int,
    ): Call<MoviesListAPI>

    @GET("{api_version}/{path_1}/{path_2}")
    fun getSearch(
        @Path("path_1") queryType1: String,
        @Path("path_2") queryType2: String,
        @Path(IMDB_NAMES_API_VERSION) apiVersion: String,
        @Query(IMDB_NAMES_API_KEY) key: String,
        @Query(IMDB_NAMES_LANG) language: String,
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean
    ): Call<MoviesListAPI>

    @GET("{api_version}/{path_1}/{path_2}")
    fun getTrends(
        @Path("path_1") queryType1: String,
        @Path("path_2") queryType2: String,
        @Path(IMDB_NAMES_API_VERSION) apiVersion: String,
        @Query(IMDB_NAMES_API_KEY) key: String,
        @Query(IMDB_NAMES_LANG) language: String,
    ): Call<MoviesListAPI>

    @GET("{api_version}/{path_1}")
    fun getConfiguration(
        @Path("path_1") queryType1: String,
        @Path(IMDB_NAMES_API_VERSION) apiVersion: String,
        @Query(IMDB_NAMES_API_KEY) key: String,
    ): Call<ConfigurationAPI>
}