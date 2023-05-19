package com.Halza.Master.presentation.ui


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.wear.compose.material.*
import com.Halza.Master.BuildConfig

import com.Halza.Master.R
import com.Halza.Master.presentation.viewmodel.MainActivityViewModel
import com.Halza.Master.presentation.utils.MainDataState
import com.Halza.Master.presentation.model.FastingData
import com.Halza.Master.presentation.theme.HalzaTheme
import com.Halza.Master.presentation.customcomponent.Chart
import com.Halza.Master.presentation.customcomponent.CustomCircularProgressIndicator
import com.Halza.Master.presentation.customcomponent.DateTimePickerDialog
import com.Halza.Master.presentation.customcomponent.GlobalDialogProvider
import com.Halza.Master.presentation.customcomponent.LostNetworkDialog
import com.Halza.Master.presentation.customcomponent.RangeTimePickerDialog
import com.Halza.Master.presentation.utils.AppConstatnt
import com.Halza.Master.presentation.utils.CommonUtil
import com.Halza.Master.presentation.utils.Debounce
import com.Halza.Master.presentation.utils.GlobalDialogUtil
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

//UI Interface That Have All Component for UI
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalWearMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainApp(
    state: MainDataState,
    viewModel: MainActivityViewModel,
    currentData: FastingData = FastingData(),
    contxt: Context
) {
    HalzaTheme {
        val listState = rememberScalingLazyListState()
        val contentModifier = Modifier
            .fillMaxWidth()
            .padding(
                PaddingValues(
                    top = 3.dp, start = 4.dp, end = 4.dp, bottom = 4.dp
                )
            )
        val coroutineScope = rememberCoroutineScope()
        val iconModifier = Modifier
            .wrapContentSize(align = Alignment.Center)
            .fillMaxWidth(1f)
        val lifecycleOwner = LocalLifecycleOwner.current

        var TargetValueProgress: Float = state.Progress

        Scaffold(

            vignette = {
                // Only show a Vignette for scrollable screens. This code lab only has one screen,
                // which is scrollable, so we show it all the time.
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator = {
                PositionIndicator(
                    scalingLazyListState = listState
                )
            },


            ) {

            LaunchedEffect(Unit) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                    listState.scrollToItem(index = 0)
                }
            }
            GlobalDialogProvider(
                modifier = Modifier.fillMaxSize(1f),
                viewModel = GlobalDialogUtil.getInstance().getViewModel()
            ) {
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(1f),
                    contentPadding = PaddingValues(0.dp),
                    state = listState,
                    autoCentering = null,


                    ) {
                    //main Page Item for Exsiting User and New User
                    if (state.userHasConnected) {

                        item {
                            Box(modifier = Modifier.fillParentMaxSize()) {
                                LaunchedEffect(listState.centerItemScrollOffset) {
                                    viewModel.scrollToFetchDataDebounce.doAction {
                                        viewModel.startFetchingData()
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .background(MaterialTheme.colors.background),


                                    ) {

                                    ShowBigProgress(
                                        TargetValueProgress,
                                        contentModifier,
                                        iconModifier,
                                        currentData,
                                        state,
                                        viewModel
                                    )
                                    ShowProgress()

                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }

                        item { Spacer(modifier = Modifier.height(20.dp)) }
                        item {
                            Box(modifier = Modifier.fillMaxWidth()) {

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,

                                    ) {


                                    FastingText(contentModifier, state)
                                    LastFastingStartTimeText(
                                        contentModifier,
                                        state.FastingStartTime,
                                        state,
                                        contxt,
                                        viewModel
                                    )
                                    ShowDivider(contentModifier)
                                    LastFastingEndTimeText(
                                        contentModifier,
                                        state.FastingEndTime,
                                        state,
                                        contxt,
                                        viewModel
                                    )
                                }
//

                            }
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                        item {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                ShowChart(iconModifier, state, viewModel)

                            }


                        }
                    }
                    //if user not connected with halza App
                    if (!state.userHasConnected) {
                        item {
                            LaunchedEffect(Unit) {
                                lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                                    viewModel.startFetchingData(AppConstatnt.NOT_CONNECTED_USER_FETCH_DATA_PERIOD)
                                }
                            }
                        }
                        item {
                            Box(modifier = Modifier.fillParentMaxSize()) {

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,


                                    ) {

                                    openAppText(contentModifier)
                                    openOnPhoneButton(viewModel = viewModel, false)


                                }
                            }
                        }


                    }
                }

            }


        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShowChart(
    modifier: Modifier = Modifier, state: MainDataState, viewModel: MainActivityViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                PaddingValues(
                    top = 20.dp, start = 0.dp, end = 0.dp, bottom = 10.dp
                )
            )
    ) {
        WeekText(modifier)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingValues(
                        top = 0.dp, start = 5.dp, end = 5.dp, bottom = 1.dp
                    )
                ), contentAlignment = Alignment.Center
        ) {

            Chart(
                data = state.fastingHistoryDataList,
                height = 140.dp,
                isExpanded = true,
                bottomEndRadius = 0.dp,
                bottomStartRadius = 0.dp,
                target = state.Fastinghour.toFloat()
            ) {

            }


        }

        openOnPhoneButton(viewModel, true)
    }

}

@Composable
fun openOnPhoneButton(viewModel: MainActivityViewModel, isConnected: Boolean) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(top = 0.dp)),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .width(95.dp)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4D4B48)),

            onClick = {
                if (isConnected) {

                    viewModel.fireRemoteIntent(BuildConfig.DEEP_LINK_EXSIT_USER)
                } else {


                    viewModel.fireRemoteIntent(BuildConfig.DEEP_LINK_NEW_USER)
                }

            },

            ) {

            Text(
                text = stringResource(R.string.open_onPhone),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(

                    fontSize = 12.sp,
                )
            )
        }
    }

}


@Composable
fun ShowDivider(
    modifier: Modifier = Modifier, iconModifier: Modifier = Modifier
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {

        Divider(
            modifier = Modifier.width(40.dp),

            thickness = 0.3.dp, color = Color.White
        )
    }
}


@Composable
fun ButtonStartEnd(
    indicatorProgress: Float,
    viewModel: MainActivityViewModel,
    state: MainDataState,
    onClickButton: () -> Unit
) {
    // create variable for current time

    var buttonText by remember {
        mutableStateOf("")
    }
    buttonText = stringResource(R.string.start_txt)
    var isFastStarted by remember {
        mutableStateOf(false)
    }
    val startText: String = stringResource(R.string.start_txt)
    val endText: String = stringResource(R.string.end_txt)

    if (indicatorProgress > 0f) {
        buttonText = stringResource(R.string.end_txt)

    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(top = 8.dp)),
        horizontalArrangement = Arrangement.Center
    ) {
        // Button
        Button(
            modifier = Modifier
                .width(75.dp)
                .height(30.dp),

            onClick = {
                if (buttonText == startText) {
                    buttonText = endText
                    viewModel.startFasting()

                } else if (buttonText == endText) {
                    buttonText = startText
                    viewModel.endFasting()

                }
                onClickButton()

            },

            ) {

            Text(text = state.startEndText, color = Color.White)
        }
    }


}


@Composable
fun TotalFastingTimeText(modifier: Modifier = Modifier, TotalHours: String) {


    Text(

        modifier = modifier,
        textAlign = TextAlign.Center,
        text = TotalHours,
        color = Color.White,
        fontSize = 23.sp
    )

}

@Composable
fun openAppText(modifier: Modifier = Modifier) {
    Text(

        modifier = modifier.padding(
            top = 15.dp, bottom = 25.dp
        ),
        textAlign = TextAlign.Center,
        text = stringResource(R.string.connect_TohalzaApp),
        color = Color.White,
        fontSize = 15.sp
    )
}

@Composable
fun WeekText(modifier: Modifier = Modifier) {
    Text(

        modifier = modifier.padding(
            top = 15.dp
        ),
        textAlign = TextAlign.Center,
        text = stringResource(R.string.week_txt),
        color = Color.White,
        fontSize = 18.sp
    )
}

@Composable
fun FastingText(modifier: Modifier = Modifier, state: MainDataState) {
    var fastingText by remember {
        mutableStateOf("")
    }
    fastingText = stringResource(R.string.fasting_txt)

    val NextfastingText: String = state.NextFastingText
    val endText: String = stringResource(R.string.end_txt)
    Text(

        modifier = modifier,
        textAlign = TextAlign.Center,
        text = state.NextFastingText.toString(),
        color = Color.White,
        fontSize = 22.sp
    )
}


@Composable
fun LastFastingStartTimeText(
    modifier: Modifier = Modifier,
    startFastingTime: String,
    state: MainDataState,
    context: Context,
    viewModel: MainActivityViewModel
) {
    val showingFastingData = viewModel.getShowingFasting()
    val previousFastingData = viewModel.getPreviousFasting()

    val pickingDateTime = CommonUtil.parseDateTime(showingFastingData.startFasting!!)!!
    val minDateTime =
        if (previousFastingData?.endFasting != null) CommonUtil.parseDateTime(previousFastingData.endFasting) else null
    val maxDateTime = CommonUtil.today()
    val pickerTitle = stringResource(id = R.string.start_fasting)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(top = 8.dp)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(

            modifier = Modifier
                .width(90.dp)
                .height(30.dp),
            textAlign = TextAlign.Center,
            text = startFastingTime,
            color = MaterialTheme.colors.primary,
            fontSize = 20.sp
        )
        if (state.editVisiable) {
            IconButton(modifier = Modifier.size(24.dp), onClick = {
                DateTimePickerDialog.Builder(context, pickingDateTime)
                    .title(pickerTitle)
                    .minDate(minDateTime)
                    .maxDate(maxDateTime)
                    .onPickComplete { res ->
                        viewModel.updateTimeDataStartFasting(
                            CommonUtil.dateTimeToISOString(res)
                        )
                    }.show()
            }) {
                Icon(
                    Icons.Filled.Edit,
                    "contentDescription",
                )
            }
        }
    }

}

@Composable
fun LastFastingEndTimeText(
    modifier: Modifier = Modifier,
    Endfastingtime: String,
    state: MainDataState,
    context: Context,
    viewModel: MainActivityViewModel
) {
    val showingFastingData = viewModel.getShowingFasting()

    val pickingDateTime = CommonUtil.parseDateTime(showingFastingData.expectedEndFasting)!!
    val minDateTime = CommonUtil.today().plusMinutes(1)
    val pickerTitle = stringResource(id = R.string.end_fasting)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(top = 8.dp)),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(

            modifier = Modifier
                .width(90.dp)
                .height(35.dp),
            textAlign = TextAlign.Center,
            text = Endfastingtime,
            color = MaterialTheme.colors.primary,
            fontSize = 20.sp
        )
        if (state.editVisiable) {
            IconButton(modifier = Modifier.size(24.dp), onClick = {
                DateTimePickerDialog.Builder(context, pickingDateTime)
                    .title(pickerTitle)
                    .minDate(minDateTime)
                    .onPickComplete { res ->
                        viewModel.updateTimeDataEndFasting(
                            CommonUtil.dateTimeToISOString(res)
                        )
                    }.show()
            }) {
                Icon(
                    Icons.Filled.Edit,
                    "contentDescription",
                )
            }
        }
    }
}


@Composable
fun ShowBigProgress(
    indicatorProgress: Float,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    currentData: FastingData,
    state: MainDataState,
    viewModel: MainActivityViewModel
) {

    var progress by remember {
        mutableStateOf(0f)
    }
    val remainingTextStyle: TextStyle = TextStyle(

        fontSize = 10.sp
    )


// It remembers the data usage value
    var dataUsageRemember by remember {
        mutableStateOf(-1f)
    }

    var totalTimeFasting by remember {
        mutableStateOf(state.fastingPeriod)
    }
    var FastingText by remember {
        mutableStateOf("Fasting")
    }


    val millisInFuture: Long = 16 * 1000 * 3600 // TODO: get actual value

    val timeData = remember {
        mutableStateOf(millisInFuture)
    }
    var timeDataString = remember {
        mutableStateOf("")
    }

    var progressAnimDuration by remember {
        mutableStateOf(3600)
    }

    var textAnimationDuration by remember {
        mutableStateOf(1500)
    }
    // This is to animate the foreground indicator
    val dataUsageAnimate = animateFloatAsState(
        targetValue = dataUsageRemember, animationSpec = tween(
            durationMillis = textAnimationDuration
        )
    )
    val animatedProgress = animateFloatAsState(
        targetValue = progress,

        animationSpec = tween(
            durationMillis = progressAnimDuration, easing = FastOutSlowInEasing
        ), visibilityThreshold = 0.001f
    ).value




    CustomCircularProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(8.dp))
            .clip(CutCornerShape(20.dp)),
        startAngle = 270f,
        endAngle = 270f,
        strokeWidth = 14.dp,
        trackColor = Color.White,


        )
    LaunchedEffect(key1 = indicatorProgress) {
        progress = indicatorProgress
    }

    // This is to start the animation when the activity is opened
    LaunchedEffect(key1 = indicatorProgress) {
        dataUsageRemember = progress * 100
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {

        TotalFastingTimeText(modifier, state.fastingPeriod)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Text(
                textAlign = TextAlign.Center,
                color = Color.White,
                text = FastingText,
                fontSize = 12.sp

            )
            if (state.showingPercntage) {
                Text(
                    modifier = Modifier.padding(
                        top = 3.dp, start = 5.dp, end = 5.dp, bottom = 5.dp
                    ),
                    text = (dataUsageAnimate.value).toInt().toString() + " %",
                    color = Color(0xFFFFA500),
                    textAlign = TextAlign.Center,
                    style = remainingTextStyle

                )
            }

        }


        ButtonStartEnd(indicatorProgress, viewModel, state) {
            if (progress == 0f) {
                state.editVisiable = true
                progressAnimDuration = (state.Fastinghour.toInt()) * 3600 * 1000

                textAnimationDuration = state.Fastinghour.toInt() * 3600 * 1000
                dataUsageRemember = progress * 100
//                totalTimeFasting=(currentData.fastingHr.toInt()-1).toString()+":59"

//                countDownTimer.start()


            } else {
                state.editVisiable = false
                progressAnimDuration = 500
                progress = 0f
                textAnimationDuration = 500
                dataUsageRemember = progress * 100


//                countDownTimer.cancel()
                timeData.value = millisInFuture
            }


        }


        LaunchedEffect(key1 = state.nextFastingTime) {
            FastingText = state.nextFastingTime
        }

        LaunchedEffect(key1 = state.fastingPeriod) {
            totalTimeFasting = state.fastingPeriod
        }
    }


}


@Composable
fun ShowProgress() {

    CircularProgressIndicator(
        progress = 1.0f,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 28.dp, start = 12.dp, end = 12.dp, bottom = 28.dp
            ),
        startAngle = 290f,
        endAngle = 290f,
        strokeWidth = 5.dp,
        indicatorColor = MaterialTheme.colors.primary,


        )


}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {

}
