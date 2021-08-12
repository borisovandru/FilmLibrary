package com.android.filmlibrary.view.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.filmlibrary.R
import com.android.filmlibrary.viewmodel.favorite.FavoriteViewModel

class FavoriteFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
    }

}