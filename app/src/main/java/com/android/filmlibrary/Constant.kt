package com.android.filmlibrary

object Constant {
    const val MOVIES_ADAPTER_COUNT_SPAN = 1
    const val MOVIES_ADAPTER_COUNT_SPAN2 = 3
    const val COUNT_MOVIES_BY_CATEGORY = 20
    const val COUNT_MOVIES_BY_TREND = 20
    const val BASE_API_URL: String = "https://api.themoviedb.org/"
    const val VERSION_API: String = "3"

    const val LANG_VALUE: String = "ru-RU"

    const val URL_GENRES_1: String = "genre"
    const val URL_GENRES_2: String = "movie"
    const val URL_GENRES_3: String = "list"

    const val URL_MOVIES_BY_GENRE_DIR_1: String = "discover"
    const val URL_MOVIES_BY_GENRE_DIR_2: String = "movie"

    const val URL_SEARCH_1: String = "search"
    const val URL_SEARCH_2: String = "movie"

    const val URL_TRENDS_1: String = "movie"

    const val URL_NOW_PLAYING: String = "now_playing"
    const val URL_POPULAR: String = "popular"
    const val URL_TOP_RATED: String = "top_rated"
    const val URL_UPCOMING: String = "upcoming"

    const val URL_CONF_1: String = "configuration"

    const val BASE_IMAGE_URL: String = "https://image.tmdb.org/t/p/"
    const val IMAGE_POSTER_SIZE_1: String = "w185"
    const val NAVIGATE_FROM_GENRES_TO_MOVIES_BY_GENRE: Int = R.id.action_navigation_genres_to_moviesByGenresFragment2
    const val NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO: Int = R.id.action_navigation_trends_to_movieInfoFragment
    const val NAVIGATE_FROM_GENRES_TO_MOVIE_INFO: Int = R.id.action_navigation_genres_to_movieInfoFragment2
    const val NAVIGATE_FROM_MOVIES_BY_GENRES_TO_MOVIE_INFO: Int = R.id.action_moviesByGenresFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_SEARCH_TO_MOVIE_INFO: Int = R.id.action_navigation_search_to_movieInfoFragment

    const val SERVER_ERROR = "Ошибка сервера"
    const val REQUEST_ERROR = "Ошибка запроса на сервер"
    const val CORRUPTED_DATA = "Неполные данные"
}