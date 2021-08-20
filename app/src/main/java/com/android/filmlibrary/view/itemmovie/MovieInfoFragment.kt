package com.android.filmlibrary.view.itemmovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.android.filmlibrary.Constant.BASE_IMAGE_URL
import com.android.filmlibrary.Constant.EMPTY_POSTER
import com.android.filmlibrary.Constant.FAV_ICON
import com.android.filmlibrary.Constant.FAV_ICON_BORDER
import com.android.filmlibrary.Constant.IMAGE_POSTER_SIZE_1
import com.android.filmlibrary.Constant.NAME_PARCEBLE_MOVIE
import com.android.filmlibrary.R
import com.android.filmlibrary.databinding.FragmentMovieInfoBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.Movie
import com.android.filmlibrary.view.showSnackBar
import com.android.filmlibrary.viewmodel.itemmovie.MovieInfoViewModel

class MovieInfoFragment : Fragment() {

    private val movieInfoViewModel by viewModels<MovieInfoViewModel>()

    private val viewModel: MovieInfoViewModel by lazy {
        ViewModelProvider(this).get(movieInfoViewModel::class.java)
    }

    private var _binding: FragmentMovieInfoBinding? = null
    private val binding
        get() = _binding!!

    private var movieId: Int = -1
    private var movie: Movie? = null

    private var note: String = ""

    private lateinit var buttonFavorite: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMovieInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.SuccessMovie -> {
                val movieData = data.movieAdvData

                binding.countryMovie.visibility = View.VISIBLE
                binding.progressBarCountry.visibility = View.GONE
                movieData.countries.forEach { country ->
                    if (binding.countryMovie.text.toString() == "") {
                        binding.countryMovie.text = country.name
                    } else {
                        binding.countryMovie.text = getString(
                            R.string.comma,
                            binding.countryMovie.text.toString(),
                            country.name
                        )
                    }
                }

                binding.progressBarRuntime.visibility = View.GONE
                binding.runtimeMovie.visibility = View.VISIBLE
                binding.runtimeMovie.text =
                    movieData.runtime.toString() + getString(R.string.Minutes)

                binding.genreMovie.visibility = View.VISIBLE
                binding.progressBarGenre.visibility = View.GONE
                movieData.genres.forEach { genre ->
                    if (binding.genreMovie.text == "") {
                        binding.genreMovie.text = genre.name
                    } else {
                        binding.genreMovie.text = getString(
                            R.string.comma,
                            binding.genreMovie.text.toString(),
                            genre.name
                        )
                    }
                }
            }
            is AppState.Loading -> {
                binding.progressBarCountry.visibility = View.VISIBLE
                binding.progressBarRuntime.visibility = View.VISIBLE
                binding.progressBarGenre.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.progressBarCountry.visibility = View.VISIBLE
                binding.progressBarRuntime.visibility = View.VISIBLE
                binding.progressBarGenre.visibility = View.VISIBLE
                data.error.message?.let {
                    binding.progressBarCountry.showSnackBar(it, R.string.ReloadMsg) {
                        viewModel.getMovieFromRemoteSource()
                    }
                }
            }
        }
    }

    private fun renderDataNote(data: AppState) {
        when (data) {
            is AppState.SuccessGetNote -> {
                data.note?.let {
                    note = it
                    binding.movieNote.setText(note)
                    binding.deleteButton.visibility = View.VISIBLE

                } ?: run {
                    binding.deleteButton.visibility = View.GONE
                }

            }
        }
    }

    private fun renderDataDeleteNote(data: AppState) {
        when (data) {
            is AppState.SuccessDeleteNote -> {
                binding.deleteButton.visibility = View.GONE
                binding.movieNote.setText("")
            }
        }
    }

    private fun renderDataFavorite(data: AppState) {
        when (data) {
            is AppState.SuccessAddFavorite -> {
                binding.favoriteButton.setImageResource(FAV_ICON)
            }
            is AppState.SuccessRemoveFavorite -> {
                binding.favoriteButton.setImageResource(FAV_ICON_BORDER)
            }
            is AppState.SuccessGetFavorite -> {
                if (data.idFav != 0L) {
                    binding.favoriteButton.setImageResource(FAV_ICON)
                } else {
                    binding.favoriteButton.setImageResource(FAV_ICON_BORDER)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonOk = binding.buttonOk
        buttonOk.setOnClickListener {
            val observerSetNote = Observer<AppState> { appState ->
                renderDataNote(appState)
            }
            viewModel.addNoteStart()
                .observe(viewLifecycleOwner, observerSetNote)
            viewModel.addNote(binding.movieNote.text.toString())
        }

        buttonFavorite = binding.favoriteButton

        arguments?.let { it ->
            movie = it.getParcelable(BUNDLE_EXTRA)
            movieId = movie?.id ?: 0

            movie?.let {
                if (it.posterUrl != "" && it.posterUrl != "-" && it.posterUrl != null) {
                    Glide.with(this)
                        .load(BASE_IMAGE_URL + IMAGE_POSTER_SIZE_1 + it.posterUrl)
                        .into(binding.imageMovie)
                } else {
                    binding.imageMovie.setImageResource(EMPTY_POSTER)
                }
                binding.rated.text = it.voteAverage.toString()

                if (it.title == it.originalTitle) {
                    binding.titleMovie.text = it.title
                } else {
                    binding.titleMovie.text =
                        getString(R.string.titleMovie, it.title, it.originalTitle)
                }
                binding.yearMovie.text = it.dateRelease
                binding.descrMovie.text = it.overview
            }
        }

        movieId.let {

            //Ставим наблюдателя на получения данных о фильме
            val observer = Observer<AppState> { appState ->
                renderData(appState)
            }
            viewModel.setData(movieId).observe(viewLifecycleOwner, observer)
            viewModel.getMovieFromRemoteSource()

            //Ставим наблюдателя на получения результата получения заметки
            val observerNote = Observer<AppState> { appState ->
                renderDataNote(appState)
            }
            viewModel.getNoteStart()
                .observe(viewLifecycleOwner, observerNote)
            viewModel.getNote(movieId)

            //Ставим наблюдателя на получения результата удаления заметки
            val observerDeleteNote = Observer<AppState> { appState ->
                renderDataDeleteNote(appState)
            }
            viewModel.deleteNoteStart()
                .observe(viewLifecycleOwner, observerDeleteNote)
            viewModel.deleteNote(movieId)

            //Ставим наблюдателя на получения статуса фаворита
            val observerGetFavorite = Observer<AppState> { appState ->
                renderDataFavorite(appState)
            }
            viewModel.favoriteGetStart()
                .observe(viewLifecycleOwner, observerGetFavorite)
            viewModel.favoriteGet(movieId)
        }

        val buttonDelete = binding.deleteButton
        buttonDelete.setOnClickListener {
            val observerDeleteNote = Observer<AppState> { appState ->
                renderDataDeleteNote(appState)
            }
            viewModel.deleteNoteStart()
                .observe(viewLifecycleOwner, observerDeleteNote)
            viewModel.deleteNote(movieId)
        }

        buttonFavorite.setOnClickListener {
            val observerFavorite = Observer<AppState> { appState ->
                renderDataFavorite(appState)
            }
            viewModel.favoriteSetStart()
                .observe(viewLifecycleOwner, observerFavorite)
            movie?.let { it1 -> viewModel.favoriteSet(it1) }
        }
    }

    companion object {
        const val BUNDLE_EXTRA = NAME_PARCEBLE_MOVIE

        fun newInstance(bundle: Bundle): MovieInfoFragment {
            val fragment = MovieInfoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}