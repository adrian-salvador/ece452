package com.group22.cityspots.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.group22.cityspots.model.Entry
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryScreenViewModel(private val entryId: String) : ViewModel() {
    val entry = MutableLiveData<Entry?>()
    val duplicateEntries = MutableLiveData<List<Entry>>()
    private var listenerRegistration: ListenerRegistration? = null

    init {
        loadEntry()
    }

    private fun loadEntry() {
        val documentReference = Firestore().entriesCollectionRef.document(entryId)
        listenerRegistration?.remove()

        listenerRegistration = documentReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                viewModelScope.launch {
                    val updatedEntry = snapshot.toObject<Entry>()?.also { entry.value = it }

                    updatedEntry?.placeId?.let { placeId ->
                        loadDuplicateEntries(placeId)
                    }
                }
            }
        }
    }

    private suspend fun loadDuplicateEntries(placeId: String) {
        try {
            val duplicates = Firestore().getEntriesByPlaceId(placeId)
            duplicateEntries.postValue(duplicates.filterNot { it.entryId == entryId })
            println(duplicates)
        } catch (_: Exception) {
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

class EntryScreenViewModelFactory(private val entryId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryScreenViewModel(entryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
