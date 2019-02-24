package com.tomergoldst.gepp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ui.IconGenerator
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.tomergoldst.gepp.utils.InjectorUtils
import com.tomergoldst.gepp.R
import com.tomergoldst.gepp.model.Place
import com.tomergoldst.gepp.model.Status
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException

class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        private const val MAP_DEFAULT_ZOOM = 16f
        private const val CIRCLE_STROKE_WIDTH_BAR = 1f
        private const val CIRCLE_STROKE_COLOR_ARGB = Color.DKGRAY
        private val CIRCLE_FILL_COLOR_ARGB = Color.argb(50, 150, 150, 150)
    }

    private lateinit var mModel: MainViewModel
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mListViewFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()

        // get activity view model
        mModel = ViewModelProviders.of(this, InjectorUtils.getMainViewModelProvider(application))
            .get(MainViewModel::class.java)

        // listen to model changes
        mModel.getCurrentLocation().observe(this, Observer {
            initialMapLocation(it!!)
        })

        mModel.getCurrentLocationAddress().observe(this, Observer {
            showCurrentLocationAddress(it)
        })

        mModel.getPlaces().observe(this, Observer {
            progressBar.visibility = View.GONE
            showPlacesOnMap(it)
        })

        mModel.getStatus().observe(this, Observer {
            onStatusChanged(it)
        })

        // switch between map view to list view button
        switchViewModeBtn.setOnClickListener {
            switchViewMode()
        }

        // start google map fragment
        (map as SupportMapFragment).getMapAsync(this)

        // add places list view fragment
        addListView()

    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.title = null
    }

    private fun switchViewMode() {
        if (mListViewFragment.isVisible) hideListView() else showListView()
    }

    private fun addListView() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.listContainer, PlacesListFragment.newInstance(), "placesListFragment")
            .runOnCommit {
                mListViewFragment = supportFragmentManager.findFragmentByTag("placesListFragment")!!
                hideListView()
            }
            .commit()

    }

    private fun showListView() {
        supportFragmentManager
            .beginTransaction()
            .show(mListViewFragment)
            .runOnCommit {
                switchViewModeBtn.text = getString(R.string.see_on_map)
                switchViewModeImv.setImageResource(R.drawable.ic_map_white_24dp)
                ImageViewCompat.setImageTintList(switchViewModeImv,
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)))
            }
            .commit()
    }

    private fun hideListView() {
        supportFragmentManager
            .beginTransaction()
            .hide(mListViewFragment)
            .runOnCommit {
                switchViewModeBtn.text = getString(R.string.see_on_list)
                switchViewModeImv.setImageResource(R.drawable.ic_list_white_24dp)
                ImageViewCompat.setImageTintList(switchViewModeImv,
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)))
            }
            .commit()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap ?: return

        // Attach map listeners
        googleMap.apply {
            setOnInfoWindowClickListener(this@MainActivity)
            setOnMarkerClickListener(this@MainActivity)
            setOnMapClickListener(this@MainActivity)
        }

        // Check for location runtime permission
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : BasePermissionListener() {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    mModel.init()
                }
            })
            .check()

    }

    override fun onInfoWindowClick(marker: Marker?) {
        // React to info window clicked
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // React to marker clicked

        // Return false inorder to allow default onMarkerClick behaviour
        return false
    }

    override fun onMapClick(latLng: LatLng?) {
        // React to map click
    }

    private fun initialMapLocation(location: LatLng) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.isMyLocationEnabled = true
        }

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MAP_DEFAULT_ZOOM))
        drawSearchRangeCircle(location)
        showCurrentLocationOnMap(location)
    }

    private fun drawSearchRangeCircle(
        centerPoint: LatLng,
        circleRadiusMeters: Int = MainViewModel.NEAREST_PLACES_DEFAULT_RADIUS_METERS
    ) {
        mGoogleMap.addCircle(
            CircleOptions().apply {
                center(centerPoint)
                radius(circleRadiusMeters.toDouble())
                strokeWidth(CIRCLE_STROKE_WIDTH_BAR)
                strokeColor(CIRCLE_STROKE_COLOR_ARGB)
                fillColor(CIRCLE_FILL_COLOR_ARGB)
            })
    }

    private fun showPlacesOnMap(places: List<Place>) {
        val iconGen = IconGenerator(this)

        mGoogleMap.let {
            places.forEach {
                val markerIconBmp = iconGen.makeIcon(it.name)

                mGoogleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.geometry.location.lat, it.geometry.location.lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(markerIconBmp))
                )
            }
        }
    }

    private fun showCurrentLocationOnMap(location: LatLng) {
        mGoogleMap.let {
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin_you_are_here))
            )
        }
    }

    private fun showCurrentLocationAddress(currentLocationAddress: String) {
        currentLocationAddressTxv.text = currentLocationAddress
    }

    private fun onStatusChanged(status: Status){
        when (status){
            Status.NO_INTERNET_CONNECTION -> {
                val snackbar = Snackbar.make(rootLayout, getString(R.string.error_no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction(getString(R.string.action_enable)) {
                    Toast.makeText(this@MainActivity, "Not implemented yet, Please turn it on", Toast.LENGTH_SHORT).show()
                }
                snackbar.show()
            }
            Status.NO_LOCATION_SERVICES -> {
                val snackbar = Snackbar.make(rootLayout, getString(R.string.error_location_is_disabled), Snackbar.LENGTH_LONG)
                snackbar.setAction(getString(R.string.action_enable)) {
                    Toast.makeText(this@MainActivity, "Not implemented yet, Please turn it on", Toast.LENGTH_SHORT).show()
                }
                snackbar.show()
            }
            else -> throw RuntimeException("Unknown status")
        }
    }
}
