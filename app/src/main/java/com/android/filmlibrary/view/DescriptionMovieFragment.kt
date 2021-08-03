package com.android.filmlibrary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.filmlibrary.databinding.FragmentDescriptionMovieBinding
import com.android.filmlibrary.model.data.Movie

class DescriptionMovieFragment : Fragment() {
    companion object {

        const val BUNDLE_EXTRA = "Movies"

        fun newInstance(bundle: Bundle): DescriptionMovieFragment {
            val fragment = DescriptionMovieFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: FragmentDescriptionMovieBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDescriptionMovieBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movie = arguments?.getParcelable<Movie>(BUNDLE_EXTRA)
        if (movie != null)
        with(binding){
            desImage.setImageResource(movie.image)
            desName.text = movie.name
            desDate.text = movie.date
            ("" + movie.rating + "%").also { desRating.text = it }
            desDescription.text = movie.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}