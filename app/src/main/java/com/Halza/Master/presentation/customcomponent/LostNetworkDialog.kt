package com.Halza.Master.presentation.customcomponent


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import com.Halza.Master.R

@Composable
fun LostNetworkDialog(
    onClose: () -> Unit
) {
    Alert(
        title = {
            Text(
                text = stringResource(R.string.your_devices_is_offline),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                )
            )
        },
        message = {
            Text(
                text = stringResource(R.string.all_updates_will_be_temporarily_stored_locally_please_connect_to_the_internet_for_the_new_data_synchronous_),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = TextStyle(
                    fontSize = 10.sp,
                )
            )
        }, contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 35.dp, bottom = 32.dp)
    ) {
        item {
            Button(
                onClick = onClose, modifier = Modifier
                    .size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Check, "", modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }
}