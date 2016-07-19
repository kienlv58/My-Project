package vn.k2t.traficjam.maps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.ItemData;
import vn.k2t.traficjam.model.Posts;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.Utilities;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, RoutingListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @Bind(R.id.multiple_actions)
    FloatingActionsMenu menu;
    @Bind(R.id.action_tick)
    FloatingActionButton actionTick;
    @Bind(R.id.action_see)
    FloatingActionButton actionSee;
    @Bind(R.id.action_traffic)
    FloatingActionButton actionTraffic;
    @Bind(R.id.action_accident)
    FloatingActionButton actionAccident;
    @Bind(R.id.action_pokemon)
    FloatingActionButton actionPokemon;

    private Utilities mUtilities;
    private GoogleMap mMap;
    //google map
    private Posts newPosts = new Posts();
    public static ArrayList<Posts> items = new ArrayList<>();
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    //google direction
    private LatLng from;
    private LatLng to;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorAccent, R.color.primary_dark_material_light};
    private DatabaseReference mDatabase;
    public static UserTraffic mUser = new UserTraffic();
    private SQLUser sqlUser;
    private String type = new String();
    private ProgressDialog progressDialog;
    private Uri capturedImageURI;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapss);
        mapFragment.getMapAsync(this);
        mUtilities = new Utilities(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sqlUser = new SQLUser(this);
        mUser = sqlUser.getUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AppConstants.REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    progressDialog.show();
                    Cursor cursor = getContentResolver().query(capturedImageURI,
                            new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String capturedImageFilePath = cursor.getString(index);
                    cursor.close();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bitmap = BitmapFactory.decodeFile(capturedImageFilePath, options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                    if (mUtilities.isConnected()) {
                        progressDialog.show();
                        newPosts.setImage(base64Image);
                        mDatabase.child(AppConstants.POSTS).child(mUser.getUid()).setValue(newPosts);
//                        mDatabases.child(AppConstants.POSTS).push().setValue(newPosts);
                        switch (type) {
                            case AppConstants.TYPE_TRAFFIC_JAM:
                                tickLocation(BitmapDescriptorFactory.HUE_RED);
                                break;
                            case AppConstants.TYPE_ACCIDENT:
                                tickLocation(BitmapDescriptorFactory.HUE_GREEN);
                                break;
                        }
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MapsActivity.this, getResources().getString(R.string.erro_network), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private Location mCurrentLocation;
    protected LocationManager locationManager;

    private boolean checkGPS() {
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

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
                startActivity(intent);
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

                mMap.setMapType(MAP_TYPES[1]);
                mMap.setTrafficEnabled(true);
                mMap.setMyLocationEnabled(true);


                //getMap().getUiSettings().setZoomControlsEnabled(true);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                from = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title(mUtilities.getAddressFromLatLng(latLng));
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
                //CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                //  mMap.moveCamera(center);
                //drawCircle(location);

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            } else {
                Toast.makeText(this, R.string.can_not_get_location_of_you, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestFirebase(String type) {
        saveLocationUserFromFireBase(mCurrentLocation);
        getAllLocationTrafficJam(type);
    }

    private void drawCircle(Location location) {
        MapFragMent.mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(2000).strokeWidth(2)
                .strokeColor(Color.GRAY)
                .fillColor(0x30ff0000));
    }

    public void showAlert(final double latitude, final double longtitude, final String type) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        //  alertDialog.setTitle("ban co muon chup anh khong");

        // Setting Dialog Message
        alertDialog.setMessage(R.string.you_want_camera);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        final LatLng latLng = new LatLng(latitude, longtitude);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentTakePhoto.resolveActivity(getPackageManager()) != null) {
                    @SuppressLint("SimpleDateFormat")
                    String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = "TrafficJam_" + date;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE, fileName);
                    capturedImageURI = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    newPosts.setLatitude(latitude + "");
                    newPosts.setLongitude(longtitude + "");
                    newPosts.setTitle(mUtilities.getAddressFromLatLng(latLng));
                    newPosts.setType(type);
                    newPosts.setUser_id(mUser.getUid());
                    newPosts.setName(mUser.getName());
                    newPosts.setCreated_at(Utilities.currentDate());
                    intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    startActivityForResult(intentTakePhoto, AppConstants.REQUEST_TAKE_PHOTO);
                } else {
                    Toast.makeText(MapsActivity.this, R.string.device_does_not_support_camera, Toast.LENGTH_LONG).show();

                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mUtilities.isConnected()) {
                    progressDialog.show();

//                    Posts posts = new Posts(mUtilities.getAddressFromLatLng(latLng), mUser.getName(), type, AppConstants.GOOD_RANK, latitude + "", longtitude + "", "", Utilities.currentDate());
//                    mDatabase.child(AppConstants.POSTS).child(mUser.getUid()).setValue(posts);
//
//                    Posts posts = new Posts(mUser.getUid(), mUtilities.getAddressFromLatLng(latLng), mUser.getName(), type, AppConstants.GOOD_RANK, latitude + "", longtitude + "", "", Utilities.currentDate());
//                    mDatabase.child(AppConstants.POSTS).push().setValue(posts);

                    switch (type) {
                        case AppConstants.TYPE_TRAFFIC_JAM:
                            tickLocation(BitmapDescriptorFactory.HUE_RED);
                            break;
                        case AppConstants.TYPE_ACCIDENT:
                            tickLocation(BitmapDescriptorFactory.HUE_GREEN);
                            break;
                    }
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MapsActivity.this, getResources().getString(R.string.erro_network), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void tickLocation(float a) {
        MarkerOptions markerOptions = new MarkerOptions();
        double latitude = MapFragMent.mMap.getMyLocation().getLatitude();
        double longtitude = MapFragMent.mMap.getMyLocation().getLongitude();
        LatLng latLng = new LatLng(latitude, longtitude);
        markerOptions.position(latLng);
        markerOptions.title(mUtilities.getAddressFromLatLng(latLng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(a));
        mMap.addMarker(markerOptions);
    }

    private void getAllLocationTrafficJam(final String type) {
        items.clear();
        mDatabase.child(AppConstants.POSTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Posts post = postSnapshot.getValue(Posts.class);
                    items.add(post);
                }
                showAllLocationTrafficJamInRadius3000Km(items, type);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.action_pokemon, R.id.action_accident, R.id.action_traffic})
    protected void onClickChoose(FloatingActionButton button) {
        show();
        if (button == actionAccident) {
            type = AppConstants.TYPE_ACCIDENT;

        } else if (button == actionTraffic) {
            type = AppConstants.TYPE_TRAFFIC_JAM;

        } else if (button == actionPokemon) {
            // click.selectedItem(data, AppConstants.TYPE_ACCIDENT);
        }
    }

    @OnClick({R.id.action_see, R.id.action_tick})
    protected void onClickAction(FloatingActionButton button) {
        ItemData data = new ItemData();
        double latitude = MapFragMent.mMap.getMyLocation().getLatitude();
        double longtitude = MapFragMent.mMap.getMyLocation().getLongitude();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AppConstants.KEY_LATITUDE, latitude + "");
        hashMap.put(AppConstants.KEY_LONGTITUDE, longtitude + "");
        data.setmItemData(hashMap);
        if (button == actionTick) {
            switch (type) {
                case AppConstants.TYPE_TRAFFIC_JAM:
                case AppConstants.TYPE_ACCIDENT:
                    showAlert(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), type);
                    menu.collapse();
                    break;

            }
        } else if (button == actionSee) {
            switch (type) {
                case AppConstants.TYPE_TRAFFIC_JAM:
                    mMap.clear();
                    showAllLocationTrafficJamInRadius3000Km(items, AppConstants.TYPE_TRAFFIC_JAM);
                    type = AppConstants.TYPE_TRAFFIC_JAM;
                    menu.collapse();
                    break;
                case AppConstants.TYPE_ACCIDENT:
                    showAllLocationTrafficJamInRadius3000Km(items, AppConstants.TYPE_ACCIDENT);
                    type = AppConstants.TYPE_ACCIDENT;
                    menu.collapse();
                    break;

            }

        }
    }

    private void show() {
        actionSee.setVisibility(View.VISIBLE);
        actionTick.setVisibility(View.VISIBLE);
        actionAccident.setVisibility(View.GONE);
        actionTraffic.setVisibility(View.GONE);
        actionPokemon.setVisibility(View.GONE);
    }

    private void hide() {
        actionSee.setVisibility(View.GONE);
        actionTick.setVisibility(View.GONE);
        actionAccident.setVisibility(View.VISIBLE);
        actionTraffic.setVisibility(View.VISIBLE);
        actionPokemon.setVisibility(View.VISIBLE);
    }

    public void showAllLocationTrafficJamInRadius3000Km(ArrayList<Posts> posts, String type) {
        try {
            for (Posts post : posts) {
                double latitude = Double.parseDouble(post.getLatitude());
                double longtitude = Double.parseDouble(post.getLongitude());
                if (type.equals(AppConstants.TYPE_ALL)) {
                    if (Utilities.DirectDistance(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), latitude, longtitude) < 2000d) {

                        if (post.getType().equals(AppConstants.TYPE_TRAFFIC_JAM))
                            addMarker(latitude, longtitude, BitmapDescriptorFactory.HUE_RED);
                        else if (post.getType().equals(AppConstants.TYPE_ACCIDENT))
                            addMarker(latitude, longtitude, BitmapDescriptorFactory.HUE_GREEN);

                    }
                } else {
                    float a = 0;
                    if (post.getType().equals(type)) {
                        if (type.equals(AppConstants.TYPE_ACCIDENT)) {
                            a = BitmapDescriptorFactory.HUE_GREEN;
                        } else if (type.equals(AppConstants.TYPE_TRAFFIC_JAM)) {
                            a = BitmapDescriptorFactory.HUE_RED;
                        }
                        addMarker(latitude, longtitude, a);
                    }
                }


            }
        } catch (Exception ex) {

        }
    }

    public void addMarker(double latitude, double longtitude, float a) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(latitude, longtitude);
        markerOptions.position(latLng);

        markerOptions.title(mUtilities.getAddressFromLatLng(latLng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(a));
        MapFragMent.mMap.addMarker(markerOptions);
    }

    private void saveLocationUserFromFireBase(final Location location) {
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("latitude", location.getLatitude() + "");
//        childUpdates.put("longitude", location.getLongitude() + "");
//        if (mUser != null) {
//            mDatabase.child(AppConstants.USER).child(mUser.getUid()).updateChildren(childUpdates);
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        initCamera(location);
        requestFirebase(type);
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

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int in) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(from);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        MapFragMent.mMap.moveCamera(center);
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
                Polyline polyline = MapFragMent.mMap.addPolyline(polyOptions);
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

    @Override
    public void onRoutingCancelled() {

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
    public void onConnected(@Nullable Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        initCamera(mCurrentLocation);
        requestFirebase(AppConstants.TYPE_ALL);
    }

    @Override
    public void onConnectionSuspended(int i) {
      //  mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
