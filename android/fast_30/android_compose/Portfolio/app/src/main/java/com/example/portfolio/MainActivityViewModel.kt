package com.example.portfolio

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.portfolio.model.googlegeocode.GoogleGeoCode
import com.example.portfolio.repository.GoogleRepository
import com.example.portfolio.viewmodel.BaseViewModel
import com.example.portfolio.viewmodel.DispatcherProvider
import com.example.portfolio.viewmodel.onIO
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val googleRepository: GoogleRepository,
    dispatcherProvider: DispatcherProvider
) : BaseViewModel(dispatcherProvider) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val defaultLocationRequestClient = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = 5000
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            _realTimeUserLocation = result.lastLocation
        }
    }

    var splitAddress by mutableStateOf("위치 정보를 찾을 수 없습니다.")

    // 위도 경도 데이터를 관찰하는 부분
    private var _realTimeUserLocation by mutableStateOf<Location?>(null)
    val realTimeUserLocation: Location?
        get() = _realTimeUserLocation

    private var _geocodeState by mutableStateOf<GoogleGeoCode?>(null)
    val geocodeState: GoogleGeoCode?
        get() = _geocodeState

    @SuppressLint("MissingPermission")
    fun startLocationUpdate(
        locationRequestClient: LocationRequest = defaultLocationRequestClient,
        locationCallback: LocationCallback = this.locationCallback,
    ) {
        fusedLocationClient.requestLocationUpdates(
            locationRequestClient,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    fun getLocation(callback: (location: Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnCompleteListener {
            callback(it.result)
        }
        Log.d("mainActivityViewModel", "getLocation Call")
    }


    fun getReverseGeoCode(
        returnType: String = "json",
        lat: Double,
        lng: Double,
        callback: (GoogleGeoCode) -> Unit
    ) = onIO {
        try {
            googleRepository.getReverseGeoCodeData(returnType, lat, lng).let {
                _geocodeState = it
                callback(it)
            }

            Log.d("MainActivityViewModel", "reverseGeoCode get data :: ${geocodeState?.results}")
        } catch (e: Exception) {
            Log.d("MainActivityViewModel", "reverseGeoCode error : ${e.message}")
        }
    }

    fun reverseGeoCodeCallBack(lastLocation: Location) =
        lastLocation.let {
            getReverseGeoCode(lat = it.latitude, lng = it.longitude) { geoCode ->
                splitAddress = geoCode.results.first()
                    .formattedAddress
                    .split(" ")
                    .filterIndexed { index, _ ->
                        index > 1
                    }
                    .joinToString(" ")
            }
        }
}