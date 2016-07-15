package vn.k2t.traficjam;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.k2t.traficjam.adapter.ListFriendAdapter;
import vn.k2t.traficjam.adapter.TabAdapter;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.maps.MapFragMent;
import vn.k2t.traficjam.maps.MapsActivity;
import vn.k2t.traficjam.model.Friends;
import vn.k2t.traficjam.model.ItemData;
import vn.k2t.traficjam.model.Posts;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.onclick.ItemClick;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.CommonMethod;
import vn.k2t.traficjam.untilitis.Utilities;
import vn.k2t.traficjam.user.ActivityUserProfile;
import vn.k2t.traficjam.user.LoginUserActivity;
import vn.k2t.traficjam.user.RequestFriendActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ItemClick, LocationListener, RoutingListener {

    private static final String TAG = "MainActivity";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.nav_view_right)
    NavigationView navigationView_Right;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.lv_listfriend)
    ListView lv_listfriend;
    TextView tv_friend;


    private FragmentManager manager;
    private TabAdapter tabAdapter;
    private CircleImageView imgUserProfile;
    private TextView tvNavUserName, tvNavEmail;
    public static ArrayList<Friends> listRequest;

    //firebase
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mDatabase;

    public static UserTraffic mUser;
    SQLUser sqlUser;
    private String user_uid;
    private Uri capturedImageURI;
    private ArrayList<UserTraffic> listUser;
    private ListFriendAdapter adapter;
    //FirebaseUser user;
    private Utilities mUtilities;
    private ProgressDialog progressDialog;

    //google map
    private String TYPE = new String();
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



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);



        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            initObject();
            getUserFromDB();
            //getAllFriends();
            //loadRequestFriend(mUser.getUid());


        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * generate keyhas facebook
         */
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "vn.k2t.traficjam",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    private void initializeCountDrawer(String count){

        //Gravity property aligns the text
        tv_friend.setGravity(Gravity.CENTER_VERTICAL);
        tv_friend.setTypeface(null, Typeface.BOLD);
        tv_friend.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_friend.setText(count);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mUtilities.isConnected()){
//            android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(this);
//            builder.setMessage(R.string.turn_on_wifi);
//            builder.setCancelable(false);
//            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                }
//            });
//            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    finish();
//                }
//            });
//            builder.show();
//        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        manager = getSupportFragmentManager();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView_Right.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });
        setTabFragment();
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    private void initObject() {
        tv_friend =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.request_friend));
        //initializeCountDrawer("");

        imgUserProfile = (CircleImageView) navigationView_Right.getHeaderView(0).findViewById(R.id.profile_image_user);
        tvNavUserName = (TextView) navigationView_Right.getHeaderView(0).findViewById(R.id.tv_nav_Name);
        tvNavEmail = (TextView) navigationView_Right.getHeaderView(0).findViewById(R.id.tv_nav_email);
        imgUserProfile.setOnClickListener(this);
        tvNavUserName.setOnClickListener(this);
        tvNavEmail.setOnClickListener(this);
        mUtilities = new Utilities(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        polylines = new ArrayList<>();

    }

    private void setTabFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.viewPager, FrgBase.newInstance(getApplicationContext())).commit();
        tabAdapter = new TabAdapter(manager, this);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
private void addFragMentMaps(){
    FragmentTransaction transaction = manager.beginTransaction();
    transaction.replace(android.R.id.content, FrgBase.newInstance(getApplicationContext())).commit();
}
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_contact) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, ActivityUserProfile.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.friend) {

        } else if (id == R.id.request_friend) {
           Intent intent = new Intent(MainActivity.this, RequestFriendActivity.class);
            startActivity(intent);
            // overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (id == R.id.message) {


        } else if (id == R.id.map) {
            addFragMentMaps();
        }
        else if(id == R.id.chiduong){
        }
        else if (id == R.id.acticle) {

        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override

    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.profile_image_user:
            case R.id.tv_nav_Name:
            case R.id.tv_nav_email:

                if (mUser != null) {
                    startActivityForResult(new Intent(this, ActivityUserProfile.class), 300);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_in_left);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginUserActivity.class);
                    startActivityForResult(intent, 200);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200)
            getUserFromDB();
        if (requestCode == 300) {
            getUserFromDB();
        }
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
                        switch (TYPE) {
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
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.erro_network), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void loadRequestFriend(final String uid){
        listRequest = new ArrayList<>();
        listRequest.clear();
        mDatabase.child(AppConstants.USER).child(uid).child("friends").child("friend_request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friends f = dataSnapshot.getValue(Friends.class);
                listRequest.add(f);
                String count = String.valueOf(listRequest.size());
                if (listRequest.size() != 0){
                    initializeCountDrawer(count);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listRequest.size();

    }

    public void getUserFromDB() {
        sqlUser = new SQLUser(this);
        mUser = sqlUser.getUser();
        try {
            if (mUser != null) {
                user_uid = mUser.getUid();
                tvNavUserName.setText(mUser.getName());
                tvNavEmail.setText(mUser.getEmail());
                String imagestr = mUser.getAvatar();
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        loadRequestFriend(user_uid);
                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                    }
                }.execute();


//
//                mDatabase.child(AppConstants.USER).child(user_uid).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        tvNavUserName.setText(dataSnapshot.child("name").getValue().toString());
//                        tvNavEmail.setText(dataSnapshot.child("email").getValue().toString());
//                        String imagestr = dataSnapshot.child("avatar").getValue().toString();
//
//                        if (imagestr.contains("http") || imagestr.equals("") || imagestr.equals(" ")) {
//                            CommonMethod.getInstance().loadImage(imagestr, imgUserProfile);
//                        } else {
//                            imgUserProfile.setImageBitmap(StringToBitMap(imagestr));
//                        }
//                        //cap nhat vao DB luon
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {}

//                if (imagestr.contains("http") || imagestr.equals("") || imagestr.equals(" ")) {
//                    CommonMethod.getInstance().loadImage(imagestr, imgUserProfile);
//                } else {
//                    imgUserProfile.setImageBitmap(StringToBitMap(imagestr));
//                }

                if (imagestr.contains("http")) {
                    CommonMethod.getInstance().loadImage(imagestr, imgUserProfile);
                } else{
                    imgUserProfile.setImageBitmap(StringToBitMap(imagestr));
                }

//                mDatabase.child(AppConstants.USER).child(user_uid).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        tvNavUserName.setText(dataSnapshot.child("name").getValue().toString());
//                        tvNavEmail.setText(dataSnapshot.child("email").getValue().toString());
//                        String imagestr = dataSnapshot.child("avatar").getValue().toString();
//
//                        if (imagestr.contains("http") || imagestr.equals("") || imagestr.equals(" ")) {
//                            CommonMethod.getInstance().loadImage(imagestr, imgUserProfile);
//                        } else {
//                            imgUserProfile.setImageBitmap(StringToBitMap(imagestr));
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
 //           });
            }

             else {
                imgUserProfile.setImageResource(R.drawable.bg_profile);
                tvNavUserName.setText("Đăng nhập");
                tvNavEmail.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }


    @Override
    public void selectedItem(Object obj, String type) {
        if (obj instanceof ItemData) {
            ItemData data = (ItemData) obj;
            switch (type) {
                case AppConstants.TYPE_TRAFFIC_JAM:
                    showSettingsAlert(data, type);
                    break;
                case AppConstants.TYPE_ACCIDENT:
                    showSettingsAlert(data, type);
                    break;
                case AppConstants.TYPE_SEE_TRAFFIC_JAM:
//                MapFragMent map = (MapFragMent) getSupportFragmentManager().findFragmentById(R.id.maps);
//                Location location = new Location("");
//                location.setLatitude(Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LATITUDE)));
//                location.setLongitude(Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LONGTITUDE)));
//                MapFragMent.getInstance().initCamera(location, AppConstants.TYPE_TRAFFIC_JAM);
//                MapFragMent.getInstance().setType(AppConstants.TYPE_TRAFFIC_JAM);
                    break;
                case AppConstants.TYPE_SEE_ACCIDENT:
//                MapFragMent maps = (MapFragMent) getSupportFragmentManager().findFragmentById(R.id.maps);
//                Location locations = new Location("");
//                locations.setLatitude(Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LATITUDE)));
//                locations.setLongitude(Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LONGTITUDE)));
//                maps.initCamera(locations, AppConstants.TYPE_ACCIDENT);
//                maps.setType(AppConstants.TYPE_ACCIDENT);
                    break;
            }
        } else if (obj instanceof Location) {
            mCurrentLocation = (Location) obj;
            switch (type) {
                case AppConstants.TYPE_CONNETCED:
                    initCamera(mCurrentLocation, AppConstants.TYPE_ALL);
                    break;
            }
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


    public void initCamera(Location location, String type) {
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

                MapFragMent.mMap.setMapType(MAP_TYPES[1]);
                MapFragMent.mMap.setTrafficEnabled(true);
                MapFragMent.mMap.setMyLocationEnabled(true);


                //getMap().getUiSettings().setZoomControlsEnabled(true);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                // from = new LatLng(location.getLatitude(), location.getLongitude());
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
                MapFragMent.mMap.clear();
                //getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
                MapFragMent.mMap.moveCamera(center);
                //drawCircle(location);
                saveLocationUserFromFireBase(mCurrentLocation);
                getAllLocationTrafficJam(type);
                MapFragMent.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            } else {
                Toast.makeText(this, R.string.can_not_get_location_of_you, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void drawCircle(Location location) {
        MapFragMent.mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(2000).strokeWidth(2)
                .strokeColor(Color.GRAY)
                .fillColor(0x30ff0000));
    }


    private void getAllLocationTrafficJam(final String type) {
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


    public void showAllLocationTrafficJamInRadius3000Km(ArrayList<Posts> posts, String type) {
        MapFragMent.mMap.clear();
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
                } else if (post.getType().equals(AppConstants.TYPE_TRAFFIC_JAM)) {
                    addMarker(latitude, longtitude, BitmapDescriptorFactory.HUE_RED);
                } else if (type.equals(AppConstants.TYPE_ACCIDENT)) {
                    addMarker(latitude, longtitude, BitmapDescriptorFactory.HUE_GREEN);
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
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("latitude", location.getLatitude() + "");
        childUpdates.put("longitude", location.getLongitude() + "");
        if (mUser != null) {
            mDatabase.child(AppConstants.USER).child(mUser.getUid()).updateChildren(childUpdates);
        }
    }

    public static Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void tickLocation(float a) {
        MarkerOptions markerOptions = new MarkerOptions();
        double latitude = MapFragMent.mMap.getMyLocation().getLatitude();
        double longtitude = MapFragMent.mMap.getMyLocation().getLongitude();
        LatLng latLng = new LatLng(latitude, longtitude);
        markerOptions.position(latLng);
        markerOptions.title(mUtilities.getAddressFromLatLng(latLng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(a));
        MapFragMent.mMap.addMarker(markerOptions);
    }

    public void showSettingsAlert(final ItemData data, final String type) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        //  alertDialog.setTitle("ban co muon chup anh khong");

        // Setting Dialog Message
        alertDialog.setMessage(R.string.you_want_camera);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        final double latitude = Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LATITUDE).trim());
        final double longtitude = Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LONGTITUDE).trim());
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
                    Toast.makeText(MainActivity.this, R.string.device_does_not_support_camera, Toast.LENGTH_LONG).show();

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
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.erro_network), Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

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


}

