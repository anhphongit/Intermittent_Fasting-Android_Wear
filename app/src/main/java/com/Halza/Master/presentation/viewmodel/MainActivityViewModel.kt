package com.Halza.Master.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.Halza.Master.R
import com.Halza.Master.presentation.customcomponent.DateTimePickerDialog
import com.Halza.Master.presentation.utils.MainDataState
import com.Halza.Master.presentation.model.*
import com.Halza.Master.presentation.service.IntermittentFastingRepository
import com.Halza.Master.presentation.service.SharedPreferencesService
import com.Halza.Master.presentation.utils.AppConstatnt
import com.Halza.Master.presentation.utils.CommonUtil
import com.Halza.Master.presentation.utils.Debounce
import com.Halza.Master.presentation.utils.GlobalDialogUtil
import com.Halza.Master.presentation.utils.NetworkUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    //application Context to get Device ID(NodeID)
    private val context = getApplication<Application>().applicationContext

    //model Class to bind the data with UI
    private var _state = MutableStateFlow(MainDataState())
    val state: StateFlow<MainDataState>
        get() = _state.asStateFlow()


    //Timer Data for stopwatch
    private var TimerData: Long = 0

    //object from Current Data Model
    var currentPlanData: FastingData by mutableStateOf(FastingData())

    //Counterdwon Time to pass for timer  --Depercted as the logic change
    var DownConterDuration: Long by mutableStateOf(0)

    //Get the History data (pre Records for user ) to show Chart
    var UserHistoryDataList: List<FastingData> by mutableStateOf(listOf())

    //Variable to get the DevceiD (Node ID)
    val MyNodeId: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    //Variable to get the eroor message
    var errorMessage: String by mutableStateOf("")

    //CountDown Object to start count dowm --Depercted As the Logic Change to Count up
    var countDown: CountDownTimer? = null

    //Timer task object to start stop watch countup
    var timertask: TimerTask? = null

    //Timer  object to start stop watch countup
    var timer: Timer = Timer()

    //Repository
    private val repository = IntermittentFastingRepository(context)

    //Prefs service
    private val prefService = SharedPreferencesService(context);

    fun init() {
        val storedShowingFasting = prefService.getShowingFasting()
        val storedPreviousFasting = prefService.getPreviousFasting()
        val storedNextFasting = prefService.getNextFasting()
        val storedHistoryFasting = prefService.getHistoryFasting()

        changeShowingFastingData(storedShowingFasting)

        if (storedPreviousFasting?.endFasting != null && storedPreviousFasting.endFasting != "") _state.value.PreviuosEndFastingTime =
            CommonUtil.parseDateTime(storedPreviousFasting.endFasting)!!

        if (storedNextFasting != null) {
            getNextFastingStartTimeV2(storedNextFasting.nextStartFasting)
            getCurrentFastingStartTime(storedNextFasting.nextStartFasting)
            getFastingEndTime(storedNextFasting.nextEndFasting)
        }

        renderHistoryFastingStatistic(storedHistoryFasting)

        if (!NetworkUtil.checkNetworkConnection(context)) {
            GlobalDialogUtil.getInstance().showLostNetworkDialog()
        }
    }


    // Register listen network state
    fun registerNetworkListener() {
        NetworkUtil.registerNetworkChange(context) { isAvailable, type ->
            run {
                Log.d("AnhPhong", "Network changed: isAvailable: $isAvailable, type: $type")

                if (!isAvailable) {
                    GlobalDialogUtil.getInstance().showLostNetworkDialog()
                }
            }
        }
    }

    fun getShowingFasting(): FastingData = prefService.getShowingFasting()
    fun getPreviousFasting(): FastingData? = prefService.getPreviousFasting()

    private fun changeShowingFastingData(data: FastingData): Unit {
        prefService.saveShowingFasting(data)
        GetPageData(
            data.startFasting, data.fastingHr.toLong(), data, data.endFasting
        )
    }

    //Get Current Data (Records) Cycle /Fasting For User
    fun getCurrentIntermettantEndFastingUserData(nodeId: String) {
        viewModelScope.launch {
            try {
                val response1 = repository.getCurrentFastingPlan(nodeId)
                if (response1.code() == 200) {//user connected and have plan it will show fasting in progress
                    val response = response1.body() as FastingData

                    changeShowingFastingData(response)
                    getFastingHistory()
                    GetPreviuosIntermedateFastingData(nodeId)
                    checkToSyncData(nodeId, response)
                } else if (response1.code() == 404) {//user not connected his watch with halza app
                    _state.value = state.value.copy(userHasConnected = false)
                } else if (response1.code() == 204) {//user is connected with the halza app but doesn't have any plan yet
                    _state.value = state.value.copy(NewUser = true)
                    changeShowingFastingData(FastingData())
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()
                changeShowingFastingData(prefService.getShowingFasting())
                renderHistoryFastingStatistic(prefService.getHistoryFasting())
            }
        }
    }

    private fun checkToSyncData(nodeId: String, fetchedCurrentFastingData: FastingData): Unit {
        viewModelScope.launch {
            try {
                val hasUpdateSinceLastConnect =
                    repository.checkIfHasUpdateSinceTheLastTimeConnect(fetchedCurrentFastingData)
                val hasOutOfDateData = repository.checkIfHasOutDateData()
                val syncCompleted: () -> Unit = {
                    getCurrentIntermettantEndFastingUserData(nodeId)
                    Log.d("AnhPhong", "Sync completed")
                }
                var lastFasting: FastingData? = fetchedCurrentFastingData

                if (hasOutOfDateData) {
                    if (hasUpdateSinceLastConnect) {
                        GlobalDialogUtil.getInstance().showConflictFastingDataDialog(onAccept = {
                            viewModelScope.launch {
                                lastFasting = repository.syncOutOfDateFasting(nodeId)
                                syncCompleted()
                            }
                        }, onRefuse = {
                            prefService.clearOutOfSyncFastingData()
                            syncCompleted()
                        })
                    } else {
                        lastFasting = repository.syncOutOfDateFasting(nodeId)
                        syncCompleted()
                    }
                }

                prefService.saveCurrentFasting(lastFasting)
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    fun checkIfMeetFastingLimitation(totalFastingDuration: Long) {
        if (totalFastingDuration > AppConstatnt.FASTING_PERIOD_LIMITATION) {
            endFasting()
        }
    }

    //Get Expected End Fasting
    fun getCurrentIntermettantExpectedEndFasting(nodeId: String) {
        viewModelScope.launch {
            try {
                val response1 = repository.getCurrentFastingPlan(nodeId)
                if (response1.code() == 200) {
                    val response = response1.body() as FastingData
                    getFastingEndTime(response.expectedEndFasting)
                }

            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    //Get  watch Id From Google Play Service
    fun GetDeviceIdForWatch() {

        viewModelScope.launch {

            try {
                val thread = Thread {
                    try {
                        var nodeListTask: Task<Node> = Wearable.getNodeClient(context).localNode
                        val node: Node = Tasks.await(nodeListTask)
                        MyNodeId.postValue(node.id)


                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                thread.start()


            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                exception.message.toString()
            }

        }


    }

    //Get Curent Data for user Has plan and update UI using MainDataState
    @SuppressLint("SuspiciousIndentation")
    fun GetPageData(
        startFastingDateTime: String?,
        fastingDuration: Long,
        currentPlanData: FastingData,
        endFastingTime: String?,
    ) {
        try {
            endStopWtach() // End Stop Watch to get updated Data
            _state.value =
                MainDataState()//New MainState to get updaeted data to update UI everytime calling the APi
            _state.value = state.value.copy(
                Fastinghour = fastingDuration.toInt()
            )

            val formatter = DateTimeFormatter.ISO_DATE_TIME
            if (startFastingDateTime != null) {
                getCurrentFastingStartTime(currentPlanData.startFasting)//Convert the Start fasting to binding on UI
                getFastingEndTime(currentPlanData.endFasting)//Convert the End fasting to binding on UI

                formatter.withZone(ZoneId.systemDefault())
                val startFastingDateTimeDatTime: LocalDateTime = LocalDateTime.parse(
                    startFastingDateTime, formatter
                ) /*Get the Start Fasting time to calculate the Deffreance */
                val secondZdt = ZonedDateTime.of(
                    startFastingDateTimeDatTime, ZoneId.of("UTC")
                )
                val zdtAtET = secondZdt.withZoneSameInstant(ZoneId.of("UTC"))
                    .withSecond(0) /*Convert Start fasting Time to UTC Zone*/
                val current =
                    LocalDateTime.now(ZoneId.of("UTC"))/*Get The Current Time in UTC Zone to Get Right Deffrence  on all zones */
                val firstZdt = ZonedDateTime.of(
                    current, ZoneId.of("UTC")
                )
                val tempDateTime = LocalDateTime.from(zdtAtET)
                val DiffinMillSeconds = tempDateTime.until(
                    firstZdt, ChronoUnit.MILLIS
                )/*Calculate the Diffrence in MILISeconds*/

                val fastingHoursinMilisconds =
                    fastingDuration * 3600 * 1000 /*Fasting hour in mili seconds */
                var Percntage: Double =
                    (DiffinMillSeconds.toDouble()) * 100 / (fastingHoursinMilisconds.toDouble())
                if (endFastingTime == "" || endFastingTime == null) { /*check if user didn't end the fasting */
                    if ((DiffinMillSeconds - fastingHoursinMilisconds) > 0) {/*check if user over fasting to set the condition for Timne Picker*/
                        state.value.isUserOverFasting = true
                    }
                    getCurrentFastingStartTime(currentPlanData.startFasting)/*Convert Start fasting Time to AM/PM*/
                    getFastingEndTime(currentPlanData.expectedEndFasting)/*Get Expected End Fasting  and convert  to AM/PM Format*/
                    val time =
                        DiffinMillSeconds/*get the Timer Value to Update UI with Hours and Minutes */
                    var hours = TimeUnit.MILLISECONDS.toHours(time)/*get the diff Hours */
                    var minutes =
                        TimeUnit.MILLISECONDS.toMinutes(time) % 60/*get the diff Minuts  */


                    var timeInHHMMSS = java.lang.String.format(
                        "%02d:%02d", hours, minutes
                    )/*convert to string to update UI*/
                    _state.value = state.value.copy(fastingPeriod = timeInHHMMSS)/*update UI*/
                    _state.value = state.value.copy(Percntage = Percntage.toFloat())/*update UI*/
                    var progressValue: Double = (Percntage / 100).toDouble()/*update UI*/
                    _state.value = state.value.copy(showingPercntage = false)/*update UI*/
                    state.value.Progress = progressValue.toFloat()
                    state.value.editVisiable = true/*update UI*/
                    state.value.NextFastingText =
                        context.resources.getString(R.string.fasting_txt)/*update UI*/
                    stopwatch(time)/*Start Stopwatch from with diff Time */
                    state.value.startEndText =
                        context.resources.getString(R.string.end_txt)/*update UI*/


                } else {/*in case User didn't start any cycle Yet but has plan Or */
                    endStopWtach()/*End Stopwatch to reset an */
                    _state.value = state.value.copy(fastingPeriod = "00" + ":00")
                    _state.value = state.value.copy(Progress = 0.0f)
                    state.value.editVisiable = false
                    state.value.NextFastingText =
                        context.resources.getString(R.string.next_fasting_txt)
                    getNextFastingData(false)
                    state.value.startEndText = context.resources.getString(R.string.start_txt)


                }
            } else {/*in case User didn't have any  Plan Yet*/
                endStopWtach()

                state.value.editVisiable = false
                _state.value = state.value.copy(fastingPeriod = "00" + ":00")
                _state.value = state.value.copy(Progress = 0.0f)
                state.value.NextFastingText = context.resources.getString(R.string.next_fasting_txt)
                getNextFastingData(false)
                state.value.startEndText = context.resources.getString(R.string.start_txt)
            }


        } catch (e: Exception) {

            e.message.toString()


        }
    }

    /*  //Update the start Time to (Edit start fasting Time)*/
    fun updateTimeDataStartFasting(newStartFasting: String) {
        viewModelScope.launch {
            try {
                val expectedEndFasting = CommonUtil.plusHourToDateTimeString(
                    newStartFasting, prefService.getShowingFasting().fastingHr.toLong()
                )
                val bodyRequest = UpdateTimeDataRequest(
                    newStartFasting = newStartFasting,
                    newExEndFasting = expectedEndFasting,
                    id = prefService.getShowingFasting().id
                )

                changeShowingFastingData(
                    prefService.getShowingFasting().copy(
                        startFasting = newStartFasting,
                        expectedEndFasting = expectedEndFasting
                    )
                )

                val response = repository.updateTimeData(
                    bodyRequest, MyNodeId.value.toString()
                )
                if (response.isSuccessful) {
                    endStopWtach()
                    _state.value = MainDataState()
                    getCurrentIntermettantEndFastingUserData(MyNodeId.value.toString())
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }

    }

    /*Update the End Time to (Edit End fasting Time)*/
    fun updateTimeDataEndFasting(newExpectedEndFasting: String) {
        viewModelScope.launch {
            try {
                val bodyRequest = UpdateTimeDataRequest(
                    newExEndFasting = newExpectedEndFasting,
                    id = prefService.getShowingFasting().id
                )

                changeShowingFastingData(
                    prefService.getShowingFasting().copy(expectedEndFasting = newExpectedEndFasting)
                )

                val response = repository.updateTimeData(
                    bodyRequest, MyNodeId.value.toString()
                )
                if (response.isSuccessful) {
                    _state.value = MainDataState()
                    getCurrentIntermettantEndFastingUserData(MyNodeId.value.toString())

                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }

    }

    /*Start Fasting Fun*/
    fun startFasting() {
        viewModelScope.launch {
            try {
                state.value.editVisiable = true
                _state.value = state.value.copy(Progress = 0.001f)
                val CurrentDateTime: LocalDateTime =
                    LocalDateTime.now(ZoneId.of("UTC"))// Get Cuurent Date in UTC Time
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                formatter.withZone(ZoneId.of("UTC"))
                val formattedString: String =
                    CurrentDateTime.format(formatter)//Convert Time To String to Send in Body Request
                val bodyRequest = StartFastingRequestBody(
                    startFasting = formattedString,

                    )//Body Request For Start Fasting

                state.value.nextFastingTime = context.resources.getString(R.string.fasting_txt)
                state.value.startEndText = context.resources.getString(R.string.end_txt)
                getCurrentFastingStartTime(formattedString)

                changeShowingFastingData(
                    repository.generateNewFasting(formattedString, prefService.getShowingFasting())
                )

                val response = repository.startFasting(
                    bodyRequest, MyNodeId.value.toString()
                )//Call Start Fasting Api
                if (response.isSuccessful) {
                    _state.value = state.value.copy(showingPercntage = false)//Remove Percntage

                    DownConterDuration =
                        prefService.getShowingFasting().fastingHr.toLong() * 3600 * 1000/*Countdown Timer --Depercated AS the Logic Change */
                    val CurrentDateTime1: LocalDateTime =
                        LocalDateTime.now(ZoneId.of("UTC"))/*get the current Time After response to Get the Seconds as the intial value for timer */
                    var startTimervalue =
                        (CurrentDateTime1.second * 1000).toLong()//Get Current Time Seconds As intial Value for the Timer



                    stopwatch(startTimervalue)//Start Timer from Current Time Seconds
                    _state.value =
                        state.value.copy(nextFastingTime = context.resources.getString(R.string.fasting_txt))//Update the UI (Text tp Fasting)

                    state.value.NextFastingText =
                        context.resources.getString(R.string.fasting_txt)/*Update UI to Fasting*/

                    getCurrentIntermettantExpectedEndFasting(MyNodeId.value.toString())/*Get Expected End Fasting*/
                    getCurrentIntermettantEndFastingUserData(MyNodeId.value.toString())
                } else {//In Case Erro From API
                    _state.value = state.value.copy(fastingPeriod = "00" + ":00")

                    _state.value = state.value.copy(Progress = 0.0f)
                }


            } catch (e: Exception) {

                errorMessage = e.message.toString()

            }
        }

    }

    /*   //ُEnd Fasting Fun*/
    fun endFasting() {
        viewModelScope.launch {
            try {
                state.value.editVisiable = false //Update UI Disable Edit
                _state.value = state.value.copy(Progress = 0.0f)//update Progress to 0
                state.value.startEndText =
                    context.resources.getString(R.string.start_txt) //Update Button Txt UI
                val CurrentDateTime: LocalDateTime =
                    LocalDateTime.now(ZoneId.of("UTC"))//Get Current Time in UTC
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                formatter.withZone(ZoneId.of("UTC"))
                val formattedString: String =
                    CurrentDateTime.format(formatter)//Convert the time to String
                endStopWtach()//Stop the Timer
//                endCountDwonTimer()//Stop Count Down
                val bodyRequest =
                    EndFastingRequestBody(endFasting = formattedString)//Body Request for End Fasting Call

                changeShowingFastingData(
                    prefService.getShowingFasting().copy(endFasting = formattedString)
                )

                val response = repository.stopFasting(
                    bodyRequest, MyNodeId.value.toString()
                )//Call APi For Stop Fasting
                if (response.isSuccessful) {
                    getNextFastingData(false)//Get Next Fasting Data
                    state.value.NextFastingText =
                        context.resources.getString(R.string.next_fasting_txt)//UPdate UI To Next Fasting
                    state.value.fastingPeriod = "00" + ":00"//Update UI For 0
                    _state.value = state.value.copy(fastingPeriod = "00" + ":00") //Update UI For 0
                    _state.value = state.value.copy(Progress = 0.0f)//UPdate Progress

                    getCurrentIntermettantEndFastingUserData(MyNodeId.value.toString())
                } else {//In Case Error From Call

                    _state.value = state.value.copy(fastingPeriod = "00" + ":00")
                    _state.value = state.value.copy(Progress = 0.0f)
                }


            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    /*Get History For User to Show Chart*/
    private fun getFastingHistory() {
        viewModelScope.launch {
            try {
                val response = repository.getFastingCycleHistory(
                    "1W", MyNodeId.value.toString()
                )/*Get History of Fasting*/
                if (response.isSuccessful) {
                    renderHistoryFastingStatistic(response.body() ?: listOf())
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    private fun renderHistoryFastingStatistic(hfData: List<FastingData>) {
        val todayDate: LocalDate = LocalDate.now()//Get Date For today To show 1 week
        val weekBeforeDate: LocalDate =
            LocalDate.now().plusDays(-7)//Get Date Before 8 Days to show 1 Week
        val weekDates: List<LocalDate> = getWeekDate(weekBeforeDate, todayDate)
        state.value.fastingHistoryDataList.clear()// clear the List of Data
        for (date in weekDates) {//add dates for the list
            state.value.fastingHistoryDataList[date.dayOfMonth.toString()] = 0f
        }

        UserHistoryDataList = hfData

        if (hfData.isNotEmpty()) {
            for (data in hfData) {
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val startFastingDateTimeDatTime: LocalDateTime =
                    LocalDateTime.parse(data.startFasting, formatter)
                val chartDate: String =
                    startFastingDateTimeDatTime.dayOfMonth.toString()//Get the date Day of the Date
                val ss = data.calculatedFastingDuration.toFloat()
                state.value.fastingHistoryDataList[chartDate] = state.value.fastingHistoryDataList[chartDate]!! + ss//Fill the list wuth Value to Chart

            }

        }
    }

    /*Stop Watch For Timer Count Up by adding 1 sec */
    private fun stopwatch(duration: Long) {
        var num: Long = duration
        timer = Timer()
        timertask = object : TimerTask() {
            override fun run() {
                num += 1000L
                TimerData = num
                var hours = TimeUnit.MILLISECONDS.toHours(TimerData)
                var minutes = TimeUnit.MILLISECONDS.toMinutes(TimerData) % 60
                var seconds = TimeUnit.MILLISECONDS.toSeconds(TimerData) % 60
                var timeInHHMMSS = java.lang.String.format("%02d:%02d", hours, minutes)

                _state.value = state.value.copy(fastingPeriod = timeInHHMMSS)

                checkIfMeetFastingLimitation(TimerData)
            }
        }
        timer.schedule(timertask, 0L, 1000L)

    }

    /*Start Count Down Timer --Deperecated As the Logic Change*/
    private fun startCountDownTimer(duration: Long) {
        countDown = object : CountDownTimer(duration, 1000) {

            override fun onTick(seconds: Long) {
                TimerData = seconds
                var hours = TimeUnit.MILLISECONDS.toHours(TimerData)
                // long minutes = (milliseconds / 1000) / 60;
                var minutes = TimeUnit.MILLISECONDS.toMinutes(TimerData) % 60

                // long seconds = (milliseconds / 1000);
                var seconds = TimeUnit.MILLISECONDS.toSeconds(TimerData) % 60
                var timeInHHMMSS = java.lang.String.format("%d:%02d", hours, minutes)

                _state.value = state.value.copy(fastingPeriod = timeInHHMMSS)
            }


            override fun onFinish() {

            }

        }
        countDown?.start()
    }

    //End the countdown Timer and reset the value -Deprecated
    private fun endCountDwonTimer() {

        countDown?.cancel()
        state.value.fastingPeriod = prefService.getShowingFasting().fastingHr.toString() + ":00"
        _state.value =
            state.value.copy(fastingPeriod = prefService.getShowingFasting().fastingHr.toString() + ":00")
        _state.value = state.value.copy(Progress = 0.0f)

    }

    //End the Stop Watch Count Upd and Reset the Value of the timer
    private fun endStopWtach() {

        timertask?.cancel()
        timer?.cancel()
        state.value.fastingPeriod = "00" + ":00"
        _state.value = state.value.copy(fastingPeriod = "00" + ":00")
        state.value.Progress = 0.0f


    }

    //Convert  Next Fasting Start Time for First Page (To show in the Progress page UI)
    private fun getNextFastingStartTimeV2(NextTimeFasting: String) {

        try {
            val pattern = DateTimeFormatter.ISO_DATE_TIME
//        val date = "11/17/2022 10:47:43 AM"
            val date = NextTimeFasting
            val localdatetime: LocalDateTime = LocalDateTime.parse(date, pattern)
            val zoneId: ZoneId = ZoneId.of("UTC") //Zone information


            val zdtAtAsia: ZonedDateTime =
                localdatetime.atZone(zoneId) //Local time in Asia timezone


            val zdtAtET = zdtAtAsia.withZoneSameInstant(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            val output = formatter.format(zdtAtET)
            _state.value = state.value.copy(showingPercntage = false)
            _state.value = state.value.copy(nextFastingTime = output.toString())
            state.value.nextFastingTime = output.toString()


        } catch (e: Exception) {
            e.message.toString()

        }

    }

    //Get Next Fasting Data From Bakcend
    private fun getNextFastingData(isStartFasting: Boolean) {

        viewModelScope.launch {

            try {
                val response1 = repository.getNextFastingData(MyNodeId.value.toString())
                if (response1.code() == 200) {
                    val response = response1.body() as NextDataResponse
                    if (isStartFasting) {

                        getCurrentFastingStartTime(response.nextStartFasting)
                        getFastingEndTime(response.nextEndFasting)
                    } else {

                        getNextFastingStartTimeV2(response.nextStartFasting)
                        getCurrentFastingStartTime(response.nextStartFasting)
                        getFastingEndTime(response.nextEndFasting)
                    }

                } else if (response1.code() == 404) {


                } else if (response1.code() == 204) {

                }

            } catch (e: Exception) {

                errorMessage = e.message.toString()


            }
        }

    }

    /*Convert Time to AM/PM Time Format*/
    private fun getFastingEndTime(LastEndFastingDate: String?) {

        try {
            val pattern = DateTimeFormatter.ISO_DATE_TIME
            val date = LastEndFastingDate

            val localdatetime: LocalDateTime = LocalDateTime.parse(date, pattern)
            val zoneId: ZoneId = ZoneId.of("UTC") //Zone information


            val zdtAtAsia: ZonedDateTime =
                localdatetime.atZone(zoneId) //Local time in Asia timezone


            val zdtAtET = zdtAtAsia.withZoneSameInstant(ZoneId.systemDefault())


            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

            val output = formatter.format(zdtAtET)
            state.value.FastingEndTime = output

        } catch (e: Exception) {
            e.message.toString()

        }

    }

    /*Get Dates For One Week*/
    fun getWeekDate(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {

        var date = startDate
        val list = mutableListOf<LocalDate>()
        while (date <= endDate) {
            list.add(date)
            date += Period.ofDays(1)
        }

        return list
    }


    /*Convert the Time for Next Fasting Time For AM/PM Format*/
    private fun getCurrentFastingStartTime(lastFastingStartTimetime: String?) {

        try {

            val pattern = DateTimeFormatter.ISO_DATE_TIME

            val date = lastFastingStartTimetime
            val localdatetime: LocalDateTime = LocalDateTime.parse(date, pattern)
            val zoneId: ZoneId = ZoneId.of("UTC") //Convert to UTC Zone information

            val zdtAtAsia: ZonedDateTime = localdatetime.atZone(zoneId) // time in  timezone


            val zdtAtET =
                zdtAtAsia.withZoneSameInstant(ZoneId.systemDefault())//Convert to Devic eTime Zone
            _state.value = state.value.copy(
                startFastingTime = zdtAtET.toLocalDateTime(),


                )//save start fasting Time to use on validation
            if (state.value.isUserOverFasting) {
                state.value.minHourForEnd = -1
                state.value.minMinForEnd = -1

            } else {

                state.value.minHourForEnd = zdtAtET.hour
                state.value.minMinForEnd = zdtAtET.minute
            }

            val formatter = DateTimeFormatter.ofPattern("h:mm a")
            val output = formatter.format(zdtAtET)
            state.value.FastingStartTime = output
        } catch (e: Exception) {
            e.message.toString()

        }

    }

    //Open the LInk On the phone using Remote intent
    public fun fireRemoteIntent(verificationUri: String) {
        RemoteActivityHelper(getApplication()).startRemoteActivity(
            Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = Uri.parse(verificationUri)
            }, null
        )
    }

    fun GetPreviuosIntermedateFastingData(nodeId: String) {
        viewModelScope.launch {

            try {
                val response1 = repository.getPreviousFasting(nodeId)
                if (response1.code() == 200) {
                    val response = response1.body() as FastingData
                    val pattern = DateTimeFormatter.ISO_DATE_TIME

                    val date = response.endFasting
                    val localdatetime: LocalDateTime = LocalDateTime.parse(date, pattern)
                    val zoneId: ZoneId = ZoneId.of("UTC") //Zone information

                    val zdtAtAsia: ZonedDateTime =
                        localdatetime.atZone(zoneId) //Local time in Asia timezone


                    val zdtAtET = zdtAtAsia.withZoneSameInstant(ZoneId.systemDefault())
                    _state.value =
                        state.value.copy(PreviuosEndFastingTime = zdtAtET.toLocalDateTime())
                }

            } catch (e: Exception) {

                errorMessage = e.message.toString()


            }
        }
    }
}

