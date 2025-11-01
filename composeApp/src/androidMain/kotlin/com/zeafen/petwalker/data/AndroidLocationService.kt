package com.zeafen.petwalker.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.services.LocationService
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidLocationService(
    private val context: Context
) : LocationService {

    private var locationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val listener: LocationListener = LocationListener { location ->
        _location.update {
            APILocation(location.latitude, location.longitude)
        }
    }

    private val _location: MutableStateFlow<APILocation?> = MutableStateFlow(null)
    override val location: StateFlow<APILocation?>
        get() = _location.asStateFlow()

    private var isRunning: Boolean = false

    override fun startObserving() {
        if (
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED
            && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED &&
            !isRunning
        ) {
            locationClient.requestLocationUpdates(
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000 * 60)
                    .build(),
                listener,
                Looper.getMainLooper()
            )
            isRunning = true
        }
    }

    override fun cancelObserving() {
        locationClient.removeLocationUpdates(listener)
        isRunning = false
    }
}