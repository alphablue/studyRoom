package com.example.portfolio

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.example.portfolio.ui.common.HardwareName
import com.example.portfolio.ui.common.PermissionName
import com.example.portfolio.ui.common.notification.NotificationBuilder
import com.example.portfolio.ui.screen.util.observeAsState
import com.example.portfolio.ui.screen.util.permission.PermissionCheck
import com.example.portfolio.ui.theme.PortfolioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val activityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // snapshot의 초기화를 위해 미리 불러오는 부분, viewmodel 안에서 초기 데이터 체크를
        // 위한 부분으로 state value 들을 읽는데 충돌이 없도록 한 방법이다.
        activityViewModel

        setContent {
            var permissionGrantCheck by remember { mutableStateOf(false) }

            PermissionCheck(
                permissionName = PermissionName.GPS,
                hardwareName = HardwareName.GPS,
                grantedCheck = { grant -> permissionGrantCheck = grant }
            )

            if (permissionGrantCheck) {
                activityViewModel.checkLoginState()
                NotificationBuilder(this).createDeliveryNotificationChannel(false)

                when (LocalLifecycleOwner.current.lifecycle.observeAsState()) {
                    Lifecycle.Event.ON_START -> {
                        activityViewModel.startLocationUpdate()
                        Log.d("mapView", "ON_START start")
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        activityViewModel.startLocationUpdate()
                        Log.d("mapView", "onResume start")
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        activityViewModel.stopLocationUpdate()
                        Log.d("mapView", "onPause start")
                    }
                    else -> {}
                }

                PortfolioTheme {
                    StartApp(activityViewModel)
                }
            }
        }
    }
}