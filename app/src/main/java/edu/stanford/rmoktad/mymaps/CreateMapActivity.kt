package edu.stanford.rmoktad.mymaps

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ui.IconGenerator
import edu.stanford.rmoktad.mymaps.models.Place
import edu.stanford.rmoktad.mymaps.models.UserMap
import kotlinx.android.synthetic.main.dialog_create_place.*

const private val TAG = "CreateMapActivity"
class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var markers: MutableList<Marker> = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_map)

        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let{
            Snackbar.make(it,"Long press to add a marker!", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //check if 'item' is save menu option
        if(item.itemId == R.id.miSave){
            Log.i(TAG, "Tapped on save!")
            //if empty map
            if(markers.isEmpty()){
                Toast.makeText(this, "There must be at least one marker on the map",
                    Toast.LENGTH_LONG).show()
                return true;
            }

            //create a map based on marker data we have
            //make a "Place" for each marker
            val places = markers.map{marker -> Place(marker.title, marker.snippet,
                marker.position.latitude, marker.position.longitude) }
            val userMap = UserMap(intent.getStringExtra(EXTRA_MAP_TITLE), places)
            val data = Intent()
            data.putExtra(
                EXTRA_USER_MAP,
                userMap
            )
            setResult(Activity.RESULT_OK, data) //pass data back to Main Activity
            finish() //finish this activity and go back to the parent activity
            return true;
        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener {markerToDelete ->
            Log.i(TAG, "onWindowClickListener- delete this marker")
            markers.remove(markerToDelete)
            markerToDelete.remove()
        }

        mMap.setOnMapLongClickListener {latLng ->
            Log.i(TAG, "onMapLongClickListener")
            showAlertDialog(latLng)
        }
        // Add a marker in Silicon Valley and move the camera
        val siliconValley = LatLng(37.4, -122.1)
        //Zoom level is # between 1 (whole world) and 21 (down at street level)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siliconValley, 10f))
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Create a marker")
                .setView(placeFormView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", null)
                .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description =
                placeFormView.findViewById<EditText>(R.id.etDescription).text.toString()

            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(
                    this, "Place must have non-empty title and description", Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

        val iconGenerator = IconGenerator(this);

        //Create custom icon for markers (speech bubble)
        iconGenerator.setStyle(IconGenerator.STYLE_PURPLE);
        //Put title inside speech bubble
        val bitmap = iconGenerator.makeIcon(title);
        // Use BitmapDescriptorFactory to create the marker
        val icon = BitmapDescriptorFactory.fromBitmap(bitmap);

            val marker =
                mMap.addMarker(MarkerOptions().position(latLng).title(title).snippet(description).icon(icon))
            markers.add(marker)
            dialog.dismiss()
        }

    }
}
