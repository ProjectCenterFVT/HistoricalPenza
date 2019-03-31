package com.projectcenterfvt.historicalpenza.utils

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Context
import android.location.Location
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor




fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, message, duration).show()

fun View.showSnackbar(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snackBar = Snackbar.make(this, resources.getString(messageRes), length)
    snackBar.f()
    snackBar.show()
}

fun View.showSnackbar(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snackBar = Snackbar.make(this, message, length)
    snackBar.f()
    snackBar.show()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun FragmentActivity.showDialog(dialog: DialogFragment) {
    val ft = supportFragmentManager.beginTransaction()
    val prev = supportFragmentManager.findFragmentByTag("dialog")
    if (prev != null) {
        ft.remove(prev)
    }
    ft.addToBackStack(null)

    dialog.show(ft, "dialog")
}

fun Location.getLatLng() = LatLng(latitude, longitude)

fun LatLng.distanceTo(location: LatLng): Long {
    val  radius = 6372795
    val x1 = latitude * Math.PI / 180
    val y1 = longitude * Math.PI / 180
    val x2 = location.latitude * Math.PI / 180
    val y2 = location.longitude * Math.PI / 180
    val res = Math.acos(Math.sin(x1) * Math.sin(x2) + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2)) * radius
    return res.toLong()
}

const val MARKER_ANIMATION_DURATION = 500L

fun Marker.animateMarkerTo(finalPosition: LatLng) {
    val typeEvaluator = TypeEvaluator<LatLng> { fraction, startValue, endValue ->
        interpolate(fraction, startValue, endValue)
    }
    val property = Property.of(Marker::class.java, LatLng::class.java, "position")
    val animator = ObjectAnimator.ofObject(this, property, typeEvaluator, finalPosition)
    animator.duration = MARKER_ANIMATION_DURATION
    animator.start()
}

fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
    val lat = (b.latitude - a.latitude) * fraction + a.latitude
    var lngDelta = b.longitude - a.longitude

    // Take the shortest path across the 180th meridian.
    if (Math.abs(lngDelta) > 180) {
        lngDelta -= Math.signum(lngDelta) * 360
    }
    val lng = lngDelta * fraction + a.longitude
    return LatLng(lat, lng)
}

fun Context.bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}