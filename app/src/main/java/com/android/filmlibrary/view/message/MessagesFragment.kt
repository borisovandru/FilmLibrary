package com.android.filmlibrary.view.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filmlibrary.Constant
import com.android.filmlibrary.databinding.MessagesFragmentBinding
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.viewmodel.message.MessagesViewModel

class MessagesFragment : Fragment() {

    private val messageViewModel by viewModels<MessagesViewModel>()

    private val viewModel: MessagesViewModel by lazy {
        ViewModelProvider(this).get(MessagesViewModel::class.java)
    }
    private val adapter = MessageFragmentAdapter()
    private lateinit var recyclerView: RecyclerView

    private var _binding: MessagesFragmentBinding? = null
    private val binding
        get() = _binding!!

    companion object {
        fun newInstance() = MessagesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MessagesFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun renderDataMsg(data: AppState) {
        when (data) {
            is AppState.SuccessGetMessages -> {
                binding.prMessage.visibility = View.GONE
                binding.rvMessage.visibility = View.VISIBLE
                adapter.fillMessages(data.messages)

            }
            is AppState.Loading -> {
                binding.prMessage.visibility = View.GONE
                binding.rvMessage.visibility = View.INVISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvMessage
        recyclerView.layoutManager = GridLayoutManager(context, Constant.MOVIES_ADAPTER_COUNT_SPAN)
        recyclerView.adapter = adapter

        //Ставим наблюдателя на получения результата получения заметки
        val observerNote = Observer<AppState> { appStateNote ->
            renderDataMsg(appStateNote)
        }
        viewModel.getMessagesStart()
            .observe(viewLifecycleOwner, observerNote)
        viewModel.getMessages()
    }
}