package com.Halza.Master.presentation.viewmodel

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GlobalDialogState(
    var content: @Composable () -> Unit,
    var isShowing: Boolean,
    var onDismissRequest: () -> Boolean
)

class GlobalDialogViewModel {
    private var _state = MutableStateFlow(GlobalDialogState({}, false, { false }))
    val state: StateFlow<GlobalDialogState>
        get() = _state.asStateFlow()

    fun showDialog(content: @Composable () -> Unit, cancelable: Boolean = true): Unit {
        _state.value = state.value.copy(
            content = content,
            isShowing = true,
            onDismissRequest = if (cancelable) {
                { false }
            } else {
                { true }
            }
        )
    }

    fun hideDialog(): Unit {
        _state.value = state.value.copy(isShowing = false)
    }
}