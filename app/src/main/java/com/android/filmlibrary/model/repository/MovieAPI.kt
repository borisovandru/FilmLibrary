package com.android.filmlibrary.model.repository

import com.android.filmlibrary.model.dto.FactDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPI {

    /**
     * Режим: Simple Search Movie - простой поиск фильмов на основе поисковой фразы
     * @param apiVersion ... - версия API с которой приято решение работать
     * @param key .......... - базовый ключ пользователя API key
     * @param language ..... - установка базового языка ответа
     * @return возвращает список фильмов в кинотеатрах...
     */


    /*@GET("{api_version}/movie/{standard_list}")
    fun sectionMoviesGetStandardsLists(
        @Path("api_version") apiVersion: Int,
        @Path("standard_list") standardList: String,
        @Query("api_key") key: String,
        @Query("page") page: Int,
        @Query("language") language: String,
        @Query("include_adult") includeAdult: Boolean
        //@Query("region") region: String
    ): Call<MoviesResponseTmdb>*/

    @GET("{api_version}/movie/{movieId}")
    fun getMovie(
        @Path("api_version") apiVersion: Int,
        @Path("movieId") standardList: Int,
        @Query("api_key") key: String,
        @Query("language") language: String,
    ): Call<FactDTO>

    /*@GET("movie")
    fun getMovie(
        @Header("") movieId: Int,
        @Query("api_key") token: String,
        @Query(LANG_PARAM) lang: String,
    ): Call<MovieDTO>
     */

}