package com.android.filmlibrary

object Constant {
    const val MOVIES_ADAPTER_COUNT_SPAN = 1
    const val MOVIES_ADAPTER_COUNT_SPAN2 = 3
    const val COUNT_CATEGORY = 5
    const val COUNT_MOVIES = 2000
    const val COUNT_MOVIES_BY_CATEGORY = 20
    const val COUNT_MOVIES_BY_CATEGORIES = 10
    const val READ_TIMEOUT: Int = 1000
    const val BASE_URL: String = "https://api.themoviedb.org/3/"
    const val LANG: String = "&language=en-US"
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
    const val NAVIGATE_TO_MOVIEBYCAT: Int = R.id.action_navigation_home_to_moviesByCategoryFragment
    const val NAVIGATE_TO_MOVIEINFO: Int = R.id.action_navigation_home_to_movieInfoFragment
}