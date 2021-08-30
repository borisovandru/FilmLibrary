package com.android.filmlibrary.viewmodel.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.filmlibrary.GlobalVariables
import com.android.filmlibrary.model.AppState
import com.android.filmlibrary.model.data.MessageNot
import com.android.filmlibrary.model.repository.local.db.RepositoryLocalDBImpl
import com.android.filmlibrary.model.room.EntityMessage

class MessagesViewModel : ViewModel() {
    private val liveDataToObserverGetMessages: MutableLiveData<AppState> = MutableLiveData()

    private val repositoryLocal = RepositoryLocalDBImpl(GlobalVariables.getDAO())

    private fun entityMessagesToMessages(messages: List<EntityMessage>): List<MessageNot> {
        val result = mutableListOf<MessageNot>()
        messages.forEach { m ->
            result.add(
                MessageNot(
                    m.id.toInt(),
                    m.header,
                    m.body
                )
            )
        }
        return result
    }

    fun getMessagesStart(): LiveData<AppState> {
        return liveDataToObserverGetMessages
    }

    fun getMessages() {
        liveDataToObserverGetMessages.value = AppState.Loading

        Thread {
            liveDataToObserverGetMessages.postValue(
                (AppState.SuccessGetMessages(
                    entityMessagesToMessages(repositoryLocal.getMessages())
                ))
            )
        }.start()
    }
}