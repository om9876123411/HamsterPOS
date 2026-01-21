package com.example.hamsterpos.ui.screen.settings


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamsterpos.data.dao.ExpenseDao
import com.example.hamsterpos.data.model.Expense
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExpenseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val expenseDao: ExpenseDao
) : ViewModel() {

    private val expenseId: Int = checkNotNull(savedStateHandle["expenseId"])

    val uiState: StateFlow<Expense> = expenseDao.getExpense(expenseId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Expense(description = "", amount = 0.0) // ค่าเริ่มต้น
        )

    fun updateExpense(description: String, amount: Double) {
        viewModelScope.launch {
            val updatedExpense = uiState.value.copy(description = description, amount = amount)
            expenseDao.updateExpense(updatedExpense)
        }
    }
}
