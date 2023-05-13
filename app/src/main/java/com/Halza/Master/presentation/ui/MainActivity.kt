/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.Halza.Master.presentation.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.Halza.Master.presentation.utils.AppConstatnt
import com.Halza.Master.presentation.viewmodel.MainActivityViewModel
import com.Halza.Master.presentation.utils.MainDataState
import com.Halza.Master.presentation.model.CurrentCycleFastingData
import com.Halza.Master.presentation.utils.NetworkUtil

class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainActivityViewModel>()
    override fun onResume() {
        super.onResume()

        // Register Network Listener
        viewModel.registerNetworkListener()
        //Get The device ID afor the Watch As it is the Node ID that connected with the pair Device
        viewModel.GetDeviceIdForWatch()
        //Observing (Waiting Value) the NodeId value from viewModel To get the Value and send the request to APi
        viewModel.MyNodeId.observe(this) { nodeId ->
            //Save Device Id(Node ID ) in local Storage taht allow to get it anyTime
            val sharedPreference =
                getSharedPreferences(AppConstatnt.STORAGE_NAME, Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString(AppConstatnt.NODE_ID, nodeId)
            editor.commit()
            //Get Current Data For user
            viewModel.getCurrentIntermettantEndFastingUserData(nodeId)
        }
        setContent {//set-up the UI
            val state: MainDataState by viewModel.state.collectAsState()
            MainApp(state, viewModel, CurrentCycleFastingData(), this)


        }

    }


}