package edu.stanford.rmoktad.mymaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.os.UserManagerCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import edu.stanford.rmoktad.mymaps.models.UserMap

const private val TAG = "DisplayMapActivity"
class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_map)

        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap

        supportActionBar?.title = userMap.title
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

        Log.i(TAG, "User map to render: ${userMap.title}")

        val iconGenerator = IconGenerator(this);

        //Create custom icon for markers (speech bubble)
        iconGenerator.setStyle(IconGenerator.STYLE_PURPLE);


        val boundsBuilder = LatLngBounds.Builder()
        for(place in userMap.places){
            val latLng = LatLng(place.latitude, place.longitude)
            boundsBuilder.include(latLng)
            //Put title inside speech bubble
            val bitmap = iconGenerator.makeIcon(place.title);
            // Use BitmapDescriptorFactory to create the marker
            val icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description).icon(icon))
        }
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
          mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))
    }
}
