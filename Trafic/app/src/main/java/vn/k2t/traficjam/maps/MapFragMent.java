package vn.k2t.traficjam.maps;

/**
 * Created by root on 07/07/2016.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.model.UserTraffic;

/**
 * Created by Paul on 8/11/15.
 */
public class MapFragMent extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, android.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 1;
    private UserTraffic mUser;
    protected LocationManager locationManager;
    public static GoogleMap mMap;
    DatabaseReference mDatabase;
    FirebaseUser user;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mUser = MainActivity.mUser;
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mMap = getMap();
        initObject();
        initListeners();
    }

    private void initListeners() {

        getMap().setOnMarkerClickListener(this);
        getMap().setOnMapLongClickListener(this);
        getMap().setOnInfoWindowClickListener(this);
        getMap().setOnMapClickListener(this);
    }

    private void initObject() {
        locationManager = (LocationManager) getActivity()
                .getSystemService(getActivity().LOCATION_SERVICE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    private void removeListeners() {
        if (getMap() != null) {
            getMap().setOnMarkerClickListener(null);
            getMap().setOnMapLongClickListener(null);
            getMap().setOnInfoWindowClickListener(null);
            getMap().setOnMapClickListener(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListeners();
    }

    public void initCamera(Location location) {
        if (!checkGPS()) {
            showSettingsAlert();
        } else {
            if (location != null) {
                CameraPosition position = CameraPosition.builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(15.2f)
                        .bearing(0.0f)
                        .tilt(0.0f)
                        .build();

                mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
                mMap.setTrafficEnabled(true);
                mMap.setMyLocationEnabled(true);


                //getMap().getUiSettings().setZoomControlsEnabled(true);
                // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //MarkerOptions options = new MarkerOptions().position(latLng);
                //options.title(getAddressFromLatLng(latLng));
//                if (mUser != null) {
//                    if (mUser.getAvatar() != "") {
//                    } else {
//                        options.icon(BitmapDescriptorFactory
//                                .fromBitmap(BitmapFactory
//                                        .decodeResource(getResources(), R.mipmap.ic_launcher)));
//                    }
//                } else {
//                    options.icon(BitmapDescriptorFactory
//                            .fromBitmap(BitmapFactory
//                                    .decodeResource(getResources(), R.mipmap.ic_launcher)));
//                }
                //getMap().addMarker(options);

                //getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

                //  drawCircle(location);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.can_not_get_location_of_you), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void drawCircle(Location location) {
        getMap().addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(200).strokeWidth(2)
                .strokeColor(Color.GRAY)
                .fillColor(0x30ff0000));
    }

//    public void addMarker(String title, Location location) {
//        MarkerOptions markerOptions = new MarkerOptions();
//        MarkerOptions options = new MarkerOptions();
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        options.position(latLng);
//        options.title(title);
//        markerOptions.title(title);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//        mMap.addMarker(markerOptions);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//    }

    public void addMarker(String title, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

    }

    private boolean checkGPS() {


        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        initCamera(mCurrentLocation);
        saveLocationUserFromFireBase(mCurrentLocation);
//        if (mUser.getAvatar() == "") {
        // LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //  Resources res = getActivity().getResources();
        // }
    }

    private void saveLocationUserFromFireBase(final double laitude, final double longitude) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("latitude", laitude + "");
        childUpdates.put("longitude", longitude + "");
        mDatabase.child(user.getUid()).updateChildren(childUpdates);
    }

    private void saveLocationUserFromFireBase(final Location location) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("latitude", location.getLatitude() + "");
        childUpdates.put("longitude", location.getLongitude() + "");
        mDatabase.child(user.getUid()).updateChildren(childUpdates);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), getActivity().getString(R.string.can_not_get_location_of_you), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Clicked on marker", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.defaultMarker());
        getMap().addMarker(options);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        getMap().addMarker(options);

    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity());

        String address = "";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
        }

        return address;
    }

    @Override
    public void onLocationChanged(Location location) {
        saveLocationUserFromFireBase(location);
        // initCamera(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
