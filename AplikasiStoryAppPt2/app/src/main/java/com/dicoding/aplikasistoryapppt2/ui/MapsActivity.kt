package com.dicoding.aplikasistoryapppt2.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.aplikasistoryapppt2.R
import com.dicoding.aplikasistoryapppt2.api.ApiConfig
import com.dicoding.aplikasistoryapppt2.data.AllStoryResponse
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import com.dicoding.aplikasistoryapppt2.data.UserPreference
import com.dicoding.aplikasistoryapppt2.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mUserPreference: UserPreference
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserPreference = UserPreference(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val token = "Bearer " + mUserPreference.getUser().token.toString()
        val client = ApiConfig.getApiService().getAllStories(token, null, null, 1)
        client.enqueue(object : Callback<AllStoryResponse> {
            override fun onResponse(
                call: Call<AllStoryResponse>,
                response: Response<AllStoryResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    addManyMarker(responseBody.listStory)
                } else {
                    Log.i("LoadFailed", response.message())
                }
            }

            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                Log.i("ClientFailed", "onFailure: ${t.message}")
            }

        })
    }

    private fun addManyMarker(story: List<ItemStoryResponse>) {
        story.forEach {
            val latLng = LatLng(it.lat as Double, it.lon as Double)
            mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                1
            )
        )
    }
}