package com.Halza.Master.presentation.customcomponent


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
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
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import com.Halza.Master.R

@Composable
fun ConflictFastingDataDialog(
    onAccept: () -> Unit,
    onRefuse: () -> Unit
) {
    Alert(
        title = {},
        message = {
            Text(
                text = stringResource(R.string.there_is_an_update_on_your_phone_do_you_want_to_override_it_),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = TextStyle(
                    fontSize = 10.sp,
                )
            )
        }, contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 35.dp, bottom = 32.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = onRefuse,
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        Icons.Rounded.Close, "", modifier = Modifier
                            .size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onAccept, modifier = Modifier
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
}