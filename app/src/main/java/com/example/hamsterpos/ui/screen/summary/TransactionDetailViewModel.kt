package com.example.hamsterpos.ui.screen.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.TransactionItemDao
import com.example.hamsterpos.data.model.TransactionItem
import kotlinx.coroutines.flow.*

class TransactionDetailViewModel(
    savedStateHandle: SavedStateHandle,
    transactionItemDao: TransactionItemDao
) : ViewModel() {

    private val transactionId: Long = checkNotNull(savedStateHandle["transactionId"])

    val transactionItems: StateFlow<List<TransactionItem>> =
        transactionItemDao.getItemsForTransaction(transactionId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )
}
