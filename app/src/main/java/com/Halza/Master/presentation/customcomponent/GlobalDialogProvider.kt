package com.Halza.Master.presentation.customcomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.dialog.Dialog
import com.Halza.Master.presentation.viewmodel.GlobalDialogViewModel

@Composable
fun GlobalDialogProvider(
    modifier: Modifier,
    viewModel: GlobalDialogViewModel,
    content: @Composable () -> Unit
) {
    Box(modifier) {
        val state by viewModel.state.collectAsState()

        content()
        Dialog(showDialog = state.isShowing, onDismissRequest = {
            val dismissRes = state.onDismissRequest()
            if (!dismissRes) viewModel.hideDialog()
        }) {
            state.content()
        }
    }
}