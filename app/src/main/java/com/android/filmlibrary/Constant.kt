package com.android.filmlibrary

object Constant {
    const val MOVIES_ADAPTER_COUNT_SPAN = 1
    const val MOVIES_ADAPTER_COUNT_SPAN2 = 3
    const val COUNT_MOVIES_BY_CATEGORY = 20
    const val COUNT_MOVIES_BY_GENRES = 10
    const val COUNT_MOVIES_BY_TREND = 20
    const val READ_TIMEOUT: Int = 1000
    const val BASE_URL: String = "https://api.themoviedb.org/3/"
    const val LANG: String = "&language=ru-RU"
    const val URL_ITEM_MOVIE_1: String = "movie/"
    const val URL_API: String = "?api_key="
    const val URL_CATEGORIES_1: String = "genre/movie/list"
    const val MOVIES_BY_CATEGORIES_1: String = "discover/movie"
    const val MOVIES_BY_CATEGORIES_2: String = "&with_genres="
    const val URL_SETTINGS_1: String = "configuration"
    const val SETTINGS_TMDB_IMAGE_URL = "ImageURL"
    const val SETTINGS_TMDB_IMAGE_SECURE_URL = "ImageSecureURL"
    const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
    const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
    const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
    const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
    const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
    const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
    const val BASE_IMAGE_URL: String = "https://image.tmdb.org/t/p/"
    const val IMAGE_POSTER_SIZE_1: String = "w185"
    const val NAVIGATE_FROM_GENRES_TO_MOVIES_BY_GENRE: Int = R.id.action_navigation_genres_to_moviesByGenresFragment2
    const val NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO: Int = R.id.action_navigation_trends_to_movieInfoFragment
    const val NAVIGATE_FROM_GENRES_TO_MOVIE_INFO: Int = R.id.action_navigation_genres_to_movieInfoFragment2
    const val NAVIGATE_FROM_MOVIES_BY_GENRES_TO_MOVIE_INFO: Int = R.id.action_moviesByGenresFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_SEARCH_TO_MOVIE_INFO: Int = R.id.action_navigation_search_to_movieInfoFragment
    const val URL_TREND: String = "movie/"
    const val URL_LATEST: String = "latest"
    const val URL_NOW_PLAYING: String = "now_playing"
    const val URL_POPULAR: String = "popular"
    const val URL_TOP_RATED: String = "top_rated"
    const val URL_UPCOMING: String = "upcoming"
    const val URL_SEARCH_1: String = "search/movie"
    const val URL_SEARCH_2: String = "&query="
}