package com.projectcenterfvt.historicalpenza.data

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.projectcenterfvt.historicalpenza.utils.Singleton
import timber.log.Timber


class CurrentLocationListener private constructor(val context: Context) : LiveData<Location>(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        buildGoogleApiClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return

            value = locationResult.lastLocation
        }
    }

    @Synchronized
    private fun buildGoogleApiClient(appContext: Context) {
        Timber.d("Build google api client")
        googleApiClient = GoogleApiClient.Builder(appContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onActive() {
        googleApiClient.connect()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun onInactive() {
        if (googleApiClient.isConnected) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        googleApiClient.disconnect()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(connectionHint: Bundle?) {
        Timber.d("connected to google api client")

        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        value = location
                    } else {
                        Timber.e("onConnected: last location value is NULL")
                    }
                }

        if (hasActiveObservers() && googleApiClient.isConnected) {
            val locationRequest = LocationRequest().apply {
                interval = UPDATE_INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null)
        }
    }

    override fun onConnectionSuspended(cause: Int) {
        Timber.w("On Connection suspended $cause")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Timber.e("GoogleApiClient connection has failed $result")
    }

    companion object : Singleton<CurrentLocationListener, Context>(::CurrentLocationListener) {

        private const val UPDATE_INTERVAL = 5000L
        private const val FASTEST_INTERVAL = 5000L

    }
}