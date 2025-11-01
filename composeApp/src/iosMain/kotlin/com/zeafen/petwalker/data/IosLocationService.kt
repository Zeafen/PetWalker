package com.zeafen.petwalker.data

import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.services.LocationService
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyReduced
import platform.darwin.NSObject

class IosLocationService : LocationService, NSObject(), CLLocationManagerDelegateProtocol {
    private val locationManager = CLLocationManager()

    init {
        locationManager.delegate = this
        locationManager.desiredAccuracy = kCLLocationAccuracyReduced
    }


    private val _location: MutableStateFlow<APILocation?> = MutableStateFlow(null)
    override val location: StateFlow<APILocation?>
        get() = _location.asStateFlow()

    @OptIn(InternalCoroutinesApi::class, ExperimentalForeignApi::class)
    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>
    ) {
        val latestLocation = didUpdateLocations.lastOrNull()
        _location.update {
            (latestLocation as? CLLocation)?.coordinate?.useContents {
                APILocation(
                    this.latitude,
                    this.longitude
                )
            }
        }

        super.locationManager(manager, didUpdateLocations)
    }


    override fun startObserving() {
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    override fun cancelObserving() {
        locationManager.stopUpdatingLocation()
    }
}