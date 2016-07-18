package vn.k2t.traficjam;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.multidex.MultiDex;
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
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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
    public static ArrayList<Friends> listRequest = new ArrayList<>();

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

        initObject();
        getUserFromDB();


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

    private void initializeCountDrawer(String count) {

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
        tv_friend = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
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
            startActivityForResult(intent, AppConstants.REQUEST_ADD_FRIENDS);
            // overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (id == R.id.message) {


        } else if (id == R.id.map) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.chiduong) {
        } else if (id == R.id.acticle) {

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
        if (requestCode == AppConstants.REQUEST_ADD_FRIENDS) {
//            String count = String.valueOf(listRequest.size());
//            if (listRequest.size() != 0) {
//                initializeCountDrawer(count);
//            }else {
//                initializeCountDrawer("");
//            }
            loadRequestFriend(mUser.getUid());
        }


    }

    public void loadRequestFriend(final String uid) {
        listRequest.clear();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                mDatabase.child(AppConstants.USER).child(uid).child("friends").child("friend_request").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Friends f = dataSnapshot.getValue(Friends.class);
                        listRequest.add(f);
                        String count = String.valueOf(listRequest.size());
                        if (listRequest.size() != 0) {
                            initializeCountDrawer(count);
                        } else {
                            initializeCountDrawer("");
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

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                String count = String.valueOf(listRequest.size());
                if (listRequest.size() != 0) {
                    initializeCountDrawer(count);
                } else {
                    initializeCountDrawer("");
                }
            }
        }.execute();


    }

    public void getUserFromDB() {
        sqlUser = new SQLUser(this);
        mUser = sqlUser.getUser();
        try {
            if (mUser != null) {
                user_uid = mUser.getUid();
                loadRequestFriend(mUser.getUid());

                tvNavUserName.setText(mUser.getName());
                tvNavEmail.setText(mUser.getEmail());
                String imagestr = mUser.getAvatar();
                if (imagestr.contains("http")) {
                    CommonMethod.getInstance().loadImage(imagestr, imgUserProfile);
                } else {
                    imgUserProfile.setImageBitmap(StringToBitMap(imagestr));
                }
            } else {
                imgUserProfile.setImageResource(R.drawable.bg_profile);
                tvNavUserName.setText("Đăng nhập");
                tvNavEmail.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
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


}
