package com.Halza.Master.presentation.ui


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.Halza.Master.BuildConfig

import com.Halza.Master.R
import com.Halza.Master.presentation.viewmodel.MainActivityViewModel
import com.Halza.Master.presentation.utils.MainDataState
import com.Halza.Master.presentation.model.CurrentCycleFastingData
import com.Halza.Master.presentation.theme.HalzaTheme
import com.Halza.Master.presentation.customcomponent.Chart
import com.Halza.Master.presentation.customcomponent.CustomCircularProgressIndicator
import com.Halza.Master.presentation.customcomponent.GlobalDialogProvider
import com.Halza.Master.presentation.customcomponent.LostNetworkDialog
import com.Halza.Master.presentation.customcomponent.RangeTimePickerDialog
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
    currentData: CurrentCycleFastingData = CurrentCycleFastingData(),
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
                                    ) {

                                    }
                                    ShowDivider(contentModifier)
                                    LastFastingEndTimeText(
                                        contentModifier,
                                        state.FastingEndTime,
                                        state,
                                        contxt,
                                        viewModel
                                    ) {


                                    }


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
                    else {
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






                    coroutineScope.launch {
                        // Animate scroll to the 10th item
                        listState.scrollToItem(index = 0)
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
                    top = 20.dp, start = 0.dp, end = 10.dp, bottom = 10.dp
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
    viewModel: MainActivityViewModel,
    onStartfastingClickButton: () -> Unit
) {
    // Declaring and initializing a calendar
    var mCalendar = Calendar.getInstance()
    var dayofMonth by remember {
        mutableStateOf(0)
    }
    var MonthOfyear by remember {
        mutableStateOf(0)
    }
    var year by remember {
        mutableStateOf(0)
    }
//    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
//    val mMinute = mCalendar[Calendar.MINUTE]
    val CurrentDateTime: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
    val mHour = CurrentDateTime.hour
    val mMinute = CurrentDateTime.minute
    // Fetching current year, month and day
    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = CurrentDateTime.monthValue
    val mDay = CurrentDateTime.dayOfMonth
    val CurrentDateTime1: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())

    val zonedDateTime: ZonedDateTime = ZonedDateTime.of(CurrentDateTime1, ZoneId.systemDefault())

    val date: Long = zonedDateTime.toInstant().toEpochMilli()
    val zonedDateTime1: ZonedDateTime = ZonedDateTime.of(CurrentDateTime, ZoneId.systemDefault())
    val date1: Long = zonedDateTime1.toInstant().toEpochMilli()
    mCalendar.time = Date()

    // Value for storing time as a string
    val mNewStartFastingTimeTime = remember {
        mutableStateOf("")
    }
    var mTimePickerDialog: RangeTimePickerDialog
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {

        // Creating a TimePicker dialod
        mTimePickerDialog = RangeTimePickerDialog(
            context, R.style.CustomDatePickerDialog1, { _, mHour: Int, mMinute: Int ->
                mNewStartFastingTimeTime.value = "$mHour:$mMinute"
                viewModel.updateTimeDataStartFasting(mHour, mMinute, dayofMonth, MonthOfyear, year)
            }, mHour, mMinute, false
        )

        // initial values as current values (present year, month and day)
        val mDatePickerDialog = DatePickerDialog(
            context,
            R.style.CustomDatePickerDialog12,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                dayofMonth = mDayOfMonth
                MonthOfyear = mMonth + 1
                year = mYear
                dayofMonth = mDayOfMonth
                MonthOfyear = mMonth + 1
                year = mYear
                val CurrentDateTime23: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
                if ((CurrentDateTime23.dayOfMonth - dayofMonth) < 0) {

                    mTimePickerDialog.setMax(25, 25)
                } else if (CurrentDateTime23.dayOfMonth == dayofMonth) {
                    mTimePickerDialog.setMax(mHour, mMinute)
                } else if (dayofMonth == state.PreviuosEndFastingTime.dayOfMonth) {
                    mTimePickerDialog.updateValue(
                        state.PreviuosEndFastingTime.hour, state.PreviuosEndFastingTime.minute.inc()
                    )
                    mTimePickerDialog.setMin(
                        state.PreviuosEndFastingTime.hour, state.PreviuosEndFastingTime.minute.inc()
                    )
                } else {
                    mTimePickerDialog.setMax(25, 25)

                }

                mTimePickerDialog.show()
            },
            mYear,
            mMonth,
            mDay
        )
        if (state.NewUser) {

            mDatePickerDialog.getDatePicker().setMinDate(date1);
            mDatePickerDialog.getDatePicker().setMaxDate(date);
        } else {
            val zonedDateTime3: ZonedDateTime =
                ZonedDateTime.of(state.PreviuosEndFastingTime, ZoneId.systemDefault())
            val date3: Long = zonedDateTime3.toInstant().toEpochMilli()
            mDatePickerDialog.getDatePicker().setMinDate(date3);
            mDatePickerDialog.getDatePicker().setMaxDate(date);

        }

        mDatePickerDialog.setTitle(stringResource(id = R.string.start_fasting))

        mTimePickerDialog.setTitle(stringResource(id = R.string.start_fasting))

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
//                        if(mTimePickerDialog.isShowing){
//                            mTimePickerDialog.dismiss()
//
//                        }
//                        mTimePickerDialog.show()
                    mDatePickerDialog.show()
                    onStartfastingClickButton
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        "contentDescription",
                    )
                }
            }
        }
    } else {
        // Creating a TimePicker dialod
        mTimePickerDialog = RangeTimePickerDialog(
            context, R.style.CustomDatePickerDialog, { _, mHour: Int, mMinute: Int ->
                mNewStartFastingTimeTime.value = "$mHour:$mMinute"
                viewModel.updateTimeDataStartFasting(mHour, mMinute, dayofMonth, MonthOfyear, year)
            }, mHour, mMinute, false
        )
        // initial values as current values (present year, month and day)
        val mDatePickerDialog = DatePickerDialog(
            context,
            R.style.CustomDatePickerDialog,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                dayofMonth = mDayOfMonth
                MonthOfyear = mMonth + 1
                year = mYear
                val CurrentDateTime23: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
                if ((CurrentDateTime23.dayOfMonth - dayofMonth) < 0) {
                    if (dayofMonth == state.PreviuosEndFastingTime.dayOfMonth) {
                        mTimePickerDialog.updateValue(
                            state.PreviuosEndFastingTime.hour,
                            state.PreviuosEndFastingTime.minute.inc()
                        )
                        mTimePickerDialog.setMin(
                            state.PreviuosEndFastingTime.hour,
                            state.PreviuosEndFastingTime.minute.inc()
                        )
                    } else {
                        mTimePickerDialog.setMax(25, 25)
                    }

                } else if (CurrentDateTime23.dayOfMonth == dayofMonth) {
                    mTimePickerDialog.setMax(mHour, mMinute)
                } else if (dayofMonth == state.PreviuosEndFastingTime.dayOfMonth) {
                    mTimePickerDialog.updateValue(
                        state.PreviuosEndFastingTime.hour, state.PreviuosEndFastingTime.minute.inc()
                    )
                    mTimePickerDialog.setMin(
                        state.PreviuosEndFastingTime.hour, state.PreviuosEndFastingTime.minute.inc()
                    )
                } else {
                    mTimePickerDialog.setMax(25, 25)

                }

                mTimePickerDialog.show()
            },
            mYear,
            mMonth,
            mDay
        )

        if (state.NewUser) {

            mDatePickerDialog.getDatePicker().setMinDate(date1);
            mDatePickerDialog.getDatePicker().setMaxDate(date);
        } else {
            val zonedDateTime3: ZonedDateTime =
                ZonedDateTime.of(state.PreviuosEndFastingTime, ZoneId.systemDefault())
            val date3: Long = zonedDateTime3.toInstant().toEpochMilli()
            mDatePickerDialog.getDatePicker().setMinDate(date3);
            mDatePickerDialog.getDatePicker().setMaxDate(date);

        }
        mDatePickerDialog.setTitle(stringResource(id = R.string.start_fasting))

        mTimePickerDialog.setTitle(stringResource(id = R.string.start_fasting))

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
//                        mTimePickerDialog.show()
                    mDatePickerDialog.show()
                    onStartfastingClickButton
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        "contentDescription",
                    )
                }
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
    viewModel: MainActivityViewModel,
    onEndfastingClickButton: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(top = 8.dp)),
        horizontalArrangement = Arrangement.Center
    ) {
        var dayofMonth by remember {
            mutableStateOf(0)
        }

// Declaring and initializing a calendar
        var mCalendar = Calendar.getInstance()
        var mHour = remember {
            mutableStateOf(mCalendar[Calendar.HOUR_OF_DAY])
        }
        var mMinute = remember {
            mutableStateOf(mCalendar[Calendar.MINUTE])
        }
        val CurrentDateTime: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
        // Fetching current year, month and day
        val mYear = mCalendar.get(Calendar.YEAR)
        val mMonth = mCalendar[Calendar.MONTH]
        val mDay = mCalendar[Calendar.DAY_OF_MONTH]
        val CurrentDateTime1: LocalDateTime = LocalDateTime.now(ZoneId.systemDefault()).plusDays(-3)
        val zonedDateTime: ZonedDateTime =
            ZonedDateTime.of(state.startFastingTime, ZoneId.systemDefault())
        val date: Long = zonedDateTime.toInstant().toEpochMilli()
        val zonedDateTime1: ZonedDateTime =
            ZonedDateTime.of(CurrentDateTime, ZoneId.systemDefault())
        val date1: Long = zonedDateTime1.toInstant().toEpochMilli()
        mCalendar.time = Date()
        // Value for end time as a string
        val mNewEndFastingTimeTime = remember {
            mutableStateOf(Endfastingtime)
        }
        var MonthOfyear by remember {
            mutableStateOf(0)
        }
        var year by remember {
            mutableStateOf(0)
        }
        var mTimePickerDialog: RangeTimePickerDialog
        val mDatePickerDialog: DatePickerDialog
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            // Creating a TimePicker dialod
            mTimePickerDialog = RangeTimePickerDialog(
                context, R.style.CustomDatePickerDialog1, { _, mHour: Int, mMinute: Int ->
                    viewModel.updateTimeDataEndFasting(
                        mHour, mMinute, dayofMonth, MonthOfyear, year
                    )
                    mNewEndFastingTimeTime.value = "$mHour:$mMinute"
                }, mHour.value, mMinute.value, false
            )


            // initial values as current values (present year, month and day)
            mDatePickerDialog = DatePickerDialog(
                context,
                R.style.CustomDatePickerDialog12,
                { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                    dayofMonth = mDayOfMonth
                    MonthOfyear = mMonth + 1
                    year = mYear
                    if (state.isUserOverFasting) {
                        if ((dayofMonth - state.startFastingTime.dayOfMonth) == 0) {
                            mTimePickerDialog.updateValue(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )

                            mTimePickerDialog.setMin(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )
                        } else {
                            mTimePickerDialog.setMin(-1, -1)

                        }

                    } else {

                        if (dayofMonth == state.startFastingTime.dayOfMonth) {

                            mTimePickerDialog.updateValue(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )

                            mTimePickerDialog.setMin(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )

                        } else {

                            mTimePickerDialog.setMin(-1, -1)
                        }


                    }
                    mTimePickerDialog.show()
                },
                mYear,
                mMonth,
                mDay

            )
            if (state.isUserOverFasting) {

                mDatePickerDialog.datePicker.setMaxDate(date1)
                mDatePickerDialog.getDatePicker().setMinDate(date);

            } else {
                val CurrentDateTime2: LocalDateTime =
                    LocalDateTime.now(ZoneId.systemDefault()).plusDays(3)
                val zonedDateTime1: ZonedDateTime =
                    ZonedDateTime.of(CurrentDateTime2, ZoneId.systemDefault())
                val date2: Long = zonedDateTime1.toInstant().toEpochMilli()
                val zonedDateTime3: ZonedDateTime =
                    ZonedDateTime.of(state.startFastingTime, ZoneId.systemDefault())
                val date3: Long = zonedDateTime3.toInstant().toEpochMilli()
                mDatePickerDialog.getDatePicker().setMinDate(date3);
                mDatePickerDialog.getDatePicker().setMaxDate(date2);


            }

            mDatePickerDialog.setTitle(stringResource(id = R.string.end_fasting))
            mTimePickerDialog.setTitle(stringResource(id = R.string.end_fasting))


        } else {

            // Creating a TimePicker dialod
            mTimePickerDialog = RangeTimePickerDialog(
                context, R.style.CustomDatePickerDialog, { _, mHour: Int, mMinute: Int ->
                    viewModel.updateTimeDataEndFasting(
                        mHour, mMinute, dayofMonth, MonthOfyear, year
                    )
                    mNewEndFastingTimeTime.value = "$mHour:$mMinute"
                }, mHour.value, mMinute.value, false
            )

            // initial values as current values (present year, month and day)
            mDatePickerDialog = DatePickerDialog(
                context,
                R.style.CustomDatePickerDialog,
                { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                    dayofMonth = mDayOfMonth
                    MonthOfyear = mMonth + 1
                    year = mYear
                    if (state.isUserOverFasting) {
                        if ((dayofMonth - state.startFastingTime.dayOfMonth) == 0) {
                            mTimePickerDialog.updateValue(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )

                            mTimePickerDialog.setMin(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )
                        } else {
                            mTimePickerDialog.setMin(-1, -1)

                        }

                    } else {

                        if (dayofMonth == state.startFastingTime.dayOfMonth) {

                            mTimePickerDialog.updateValue(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )

                            mTimePickerDialog.setMin(
                                state.startFastingTime.hour, state.startFastingTime.minute
                            )

                        } else {

                            mTimePickerDialog.setMin(-1, -1)
                        }


                    }

                    mTimePickerDialog.show()
                },
                mYear,
                mMonth,
                mDay

            )

            if (state.isUserOverFasting) {
                mDatePickerDialog.datePicker.setMaxDate(date1)
                mDatePickerDialog.getDatePicker().setMinDate(date);

            } else {
                val CurrentDateTime2: LocalDateTime =
                    LocalDateTime.now(ZoneId.systemDefault()).plusDays(3)
                val zonedDateTime1: ZonedDateTime =
                    ZonedDateTime.of(CurrentDateTime2, ZoneId.systemDefault())
                val date2: Long = zonedDateTime1.toInstant().toEpochMilli()
                val zonedDateTime3: ZonedDateTime =
                    ZonedDateTime.of(state.startFastingTime, ZoneId.systemDefault())
                val date3: Long = zonedDateTime3.toInstant().toEpochMilli()
                mDatePickerDialog.getDatePicker().setMinDate(date3);
                mDatePickerDialog.getDatePicker().setMaxDate(date2);


            }
            mDatePickerDialog.setTitle(stringResource(id = R.string.end_fasting))
            mTimePickerDialog.setTitle(stringResource(id = R.string.end_fasting))

        }

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
//                    mTimePickerDialog.show()
                mDatePickerDialog.show()
                onEndfastingClickButton
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
    currentData: CurrentCycleFastingData,
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
