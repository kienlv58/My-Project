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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.Posts;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by Paul on 8/11/15.
 */
public class MapFragMent extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, LocationListener, RoutingListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 1;
    protected LocationManager locationManager;
    public static GoogleMap mMap;
    DatabaseReference mDatabase;
    UserTraffic user;
    SQLUser sqlUser;
    private Utilities mUtilities;
    public static ArrayList<Posts> items = new ArrayList<>();
    private LatLng from;
    private LatLng to;
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};
    private List<Polyline> polylines;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
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
        // getMap().setOnMapLongClickListener(this);
        // getMap().setOnInfoWindowClickListener(this);
        getMap().setOnMapClickListener(this);
    }

    private void initObject() {
        locationManager = (LocationManager) getActivity()
                .getSystemService(getActivity().LOCATION_SERVICE);
        sqlUser = new SQLUser(getContext());
        user = sqlUser.getUser();
        //user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mUtilities = new Utilities(getActivity());
        polylines = new ArrayList<>();

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
                        .zoom(13.5f)
                        .bearing(0.0f)
                        .tilt(0.0f)
                        .build();

                mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
                mMap.setTrafficEnabled(true);
                mMap.setMyLocationEnabled(true);


                //getMap().getUiSettings().setZoomControlsEnabled(true);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                from = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title(getAddressFromLatLng(latLng));
//                if (user != null) {
//                    if (user.getAvatar() != "") {
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
//                getMap().addMarker(options);
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
                mMap.clear();
                //getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                mMap.moveCamera(center);
                drawCircle(location);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
                saveLocationUserFromFireBase(mCurrentLocation);
                getAllLocationTrafficJam();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.can_not_get_location_of_you), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void drawCircle(Location location) {
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(1000).strokeWidth(2)
                .strokeColor(Color.GRAY)
                .fillColor(0x30ff0000));
    }

    public void addMarker(double latitude, double longtitude, float a) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(latitude, longtitude);
        markerOptions.position(latLng);

        markerOptions.title(mUtilities.getAddressFromLatLng(latLng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(a));
        mMap.addMarker(markerOptions);
    }

    public void addMarker(String title, LatLng position, float a) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(a));

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
        if (mCurrentLocation != null) {
            initCamera(mCurrentLocation);
            //    saveLocationUserFromFireBase(mCurrentLocation);
            //   getAllLocationTrafficJam();
        }
//        if (mUser.getAvatar() == "") {
        // LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //  Resources res = getActivity().getResources();
        // }
    }

    private void getAllLocationTrafficJam() {
        mDatabase.child(AppConstants.POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Posts post = postSnapshot.getValue(Posts.class);
                    items.add(post);
                }
                showAllLocationTrafficJamInRadius3000Km(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getAllFriend() {

    }

    public void showAllLocationTrafficJamInRadius3000Km(ArrayList<Posts> posts) {
        mMap.clear();
        try {
            for (Posts post : posts) {
                double latitude = Double.parseDouble(post.getLatitude());
                double longtitude = Double.parseDouble(post.getLongitude());
                Log.i("aaa", Utilities.DirectDistance(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude) + "");
                if (Utilities.DirectDistance(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude) < 3000d) {
                    if (post.getType().equals(AppConstants.TYPE_TRAFFIC_JAM))
                        addMarker(latitude, longtitude, BitmapDescriptorFactory.HUE_RED);
                    else if (post.getType().equals(AppConstants.TYPE_ACCIDENT))
                        addMarker(latitude, longtitude, BitmapDescriptorFactory.HUE_GREEN);

                }
            }
        } catch (Exception ex) {

        }
    }

    private void saveLocationUserFromFireBase(final Location location) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("latitude", location.getLatitude() + "");
        childUpdates.put("longitude", location.getLongitude() + "");
        if (user != null) {
            mDatabase.child(AppConstants.USER).child(user.getUid()).updateChildren(childUpdates);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Toast.makeText(getActivity(), getActivity().getString(R.string.can_not_get_location_of_you), Toast.LENGTH_LONG).show();
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
        to = new LatLng(latLng.latitude, latLng.longitude);
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(from, to)
                .build();
        routing.execute();
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
        initCamera(location);
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

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    private static final int[] COLORS = new int[]{R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorAccent, R.color.primary_dark_material_light};

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(from);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {
            if (route.get(i).getLength() == findMinDestination(route)) {
                int colorIndex = i % COLORS.length;
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(COLORS[colorIndex]));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(route.get(i).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);
            }
        }
        // Start marker
//        MarkerOptions options = new MarkerOptions();
//        options.position(from);
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//        mMap.addMarker(options);
//        // End marker
//        options = new MarkerOptions();
//        options.position(to);
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        mMap.addMarker(options);

    }

    private int findMinDestination(ArrayList<Route> route) {
        int min = route.get(0).getLength();
        for (int i = 0; i < route.size(); i++) {
            if (min > route.get(i).getLength()) {
                min = route.get(i).getLength();
            }
        }
        return min;
    }

    @Override
    public void onRoutingCancelled() {

    }
}
