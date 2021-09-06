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
    const val URL_GENRES_PATH: String = "with_genres"

    const val URL_MOVIES_BY_GENRE_DIR_1: String = "discover"
    const val URL_MOVIES_BY_GENRE_DIR_2: String = "movie"

    const val URL_SEARCH_1: String = "search"
    const val URL_SEARCH_2: String = "movie"

    const val URL_TRENDS_1: String = "movie"

    const val URL_PERSON_1: String = "person"

    const val URL_CREDITS_1: String = "movie"
    const val URL_CREDITS_2: String = "credits"

    const val URL_NOW_PLAYING: String = "now_playing"
    const val URL_POPULAR: String = "popular"
    const val URL_TOP_RATED: String = "top_rated"
    const val URL_UPCOMING: String = "upcoming"

    const val URL_NOW_PLAYING_NAME: String = "Сейчас смотрят"
    const val URL_POPULAR_NAME: String = "Популярные"
    const val URL_TOP_RATED_NAME: String = "Высокий рейтинг"
    const val URL_UPCOMING_NAME: String = "Ожидаются"

    const val URL_CONF_1: String = "configuration"

    const val BASE_IMAGE_URL: String = "https://image.tmdb.org/t/p/"
    const val IMAGE_POSTER_SIZE_1: String = "w185"
    const val NAVIGATE_FROM_GENRES_TO_MOVIES_BY_GENRE: Int =
        R.id.action_genresFragment_to_moviesByGenreFragment
    const val NAVIGATE_FROM_TRENDS_TO_MOVIE_INFO: Int =
        R.id.action_trendsFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_GENRES_TO_MOVIE_INFO: Int =
        R.id.action_moviesByGenreFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_MOVIES_BY_GENRES_TO_MOVIE_INFO: Int =
        R.id.action_moviesByGenreFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_SEARCH_TO_MOVIE_INFO: Int =
        R.id.action_searchFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_FAV_TO_MOVIE_INFO: Int =
        R.id.action_favoriteFragment_to_movieInfoFragment
    const val NAVIGATE_FROM_MOVIE_INFO_TO_PERSON_INFO: Int =
        R.id.action_movieInfoFragment_to_personFragment
    const val NAVIGATE_FROM_PERSON_TO_MAP: Int = R.id.action_personFragment_to_mapsFragment
    const val NAVIGATE_TO_MESSAGE: Int = R.id.messagesFragment

    const val SERVER_ERROR = "Ошибка сервера"
    const val REQUEST_ERROR = "Ошибка запроса на сервер"
    const val CORRUPTED_DATA = "Неполные данные"

    const val NAME_SHARED_PREFERENCE = "Settings"

    const val THRESHOLD = 2

    const val NAME_PARCEBLE_MOVIE = "Movie"
    const val NAME_PARCEBLE_GENRE = "category"
    const val NAME_PARCEBLE_PERSON = "person"
    const val NAME_PARCEBLE_MAP = "map"

    const val NEW_MESSAGE = "NewMessage"

    const val FORMATED_STRING_DATE_TMDB = "yyyy-MM-dd"
    const val FORMATED_STRING_YEAR = "yyyy"

    const val URL_TREND_POSITION = 2

    const val NAME_DB = "Movie.db"

    const val TMDB_NAMES_LANG = "language"
    const val TMDB_NAMES_API_VERSION = "api_version"
    const val TMDB_NAMES_API_KEY = "api_key"

    const val FAV_ICON_BORDER = R.drawable.ic_favorite_border_24
    const val FAV_ICON = R.drawable.ic_favorite_24

    const val EMPTY_POSTER = R.drawable.empty_poster

    const val PERMISSION_REQUEST_CODE = 10

    const val MAX_RESULT_GEOCODER = 1

    const val GEOFENCE_RADIUS = 150.0

    const val DEFAULT_LAT = 0.0
    const val DEFAULT_LONG = 0.0

    const val DEFAULT_U = 0.5f
    const val DEFAULT_V = 0.5f

    const val GEOFENCE_DEFAULT_WIDTH = 5f
    const val GEOFENCE_MIN_DIST = 10f
    const val GEOFENCE_MIN_TIME = 10000L
    const val GEOFENCE_MOVE_CAM = 15f
    const val GEOFENCE_PAR_1 = 150f
    const val GEOFENCE_TIMEOUT = 1000

    const val NOTIFY_CH = "2"
    const val NOTIFY_NAME = "2"

    const val MAPS_ZOOM = 15f

    const val COLOR_RED = R.color.red
    const val COLOR_PURPLE = R.color.purple_500

    const val NEW_MAIL = R.drawable.ic_new_email_24
    const val MAIL = R.drawable.ic_baseline_mail_outline_24
}