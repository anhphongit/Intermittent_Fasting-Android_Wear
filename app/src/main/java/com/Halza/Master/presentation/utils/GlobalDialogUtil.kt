package com.Halza.Master.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.Halza.Master.presentation.customcomponent.LostNetworkDialog
import com.Halza.Master.presentation.viewmodel.GlobalDialogViewModel

class GlobalDialogUtil private constructor() {
    private object Holder {
        val INSTANCE = GlobalDialogUtil()
    }

    private val viewModel = GlobalDialogViewModel()

    companion object {
        @JvmStatic
        fun getInstance(): GlobalDialogUtil {
            return Holder.INSTANCE
        }
    }

    fun getViewModel(): GlobalDialogViewModel {
        return viewModel
    }

    fun hideDialog(): Unit {
        viewModel.hideDialog()
    }

    fun showLostNetworkDialog(): Unit {
        viewModel.showDialog({
            LostNetworkDialog { viewModel.hideDialog() }
        })
    }
}