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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.k2t.traficjam.adapter.ListFriendAdapter;
import vn.k2t.traficjam.adapter.TabAdapter;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.maps.MapFragMent;
import vn.k2t.traficjam.maps.MapsActivity;
import vn.k2t.traficjam.model.ItemData;
import vn.k2t.traficjam.model.Posts;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.onclick.ItemClick;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.CommonMethod;
import vn.k2t.traficjam.untilitis.Utilities;
import vn.k2t.traficjam.user.ActivityUserProfile;
import vn.k2t.traficjam.user.LoginUserActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ItemClick {

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


    private FragmentManager manager;
    private TabAdapter tabAdapter;
    private CircleImageView imgUserProfile;
    private TextView tvNavUserName, tvNavEmail;

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
    private String TYPE = new String();
    private Posts newPosts = new Posts();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initObject();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getUserFromDB();
        getAllFriends();
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

        imgUserProfile = (CircleImageView) navigationView_Right.getHeaderView(0).findViewById(R.id.profile_image_user);
        tvNavUserName = (TextView) navigationView_Right.getHeaderView(0).findViewById(R.id.tv_nav_Name);
        tvNavEmail = (TextView) navigationView_Right.getHeaderView(0).findViewById(R.id.tv_nav_email);
        imgUserProfile.setOnClickListener(this);
        tvNavUserName.setOnClickListener(this);
        tvNavEmail.setOnClickListener(this);
        mUtilities = new Utilities(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
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

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, LoginUserActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, ActivityUserProfile.class));
            // overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

    public void getUserFromDB() {
        sqlUser = new SQLUser(this);
        mUser = sqlUser.getUser();
        try {
            if (mUser != null) {
                user_uid = mUser.getUid();

                mDatabase.child(AppConstants.USER).child(user_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tvNavUserName.setText(dataSnapshot.child("name").getValue().toString());
                        tvNavEmail.setText(dataSnapshot.child("email").getValue().toString());
                        String imagestr = dataSnapshot.child("avatar").getValue().toString();

                        if (imagestr.contains("http") || imagestr.equals("") || imagestr.equals(" ")) {
                            CommonMethod.getInstance().loadImage(imagestr, imgUserProfile);
                        } else {
                            imgUserProfile.setImageBitmap(StringToBitMap(imagestr));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                imgUserProfile.setImageResource(R.drawable.bg_profile);
                tvNavUserName.setText("Đăng nhập");
                tvNavEmail.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        user =FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user!= null) {
//            mDatabases = FirebaseDatabase.getInstance().getReference().child(user.getUid());
//            ValueEventListener eventListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    mUser = dataSnapshot.getValue(UserTraffic.class);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//            mDatabases.addValueEventListener(eventListener);
//        }
    }

    @Override
    public void selectedItem(Object obj, String type) {
        ItemData data = (ItemData) obj;
        switch (type) {
            case AppConstants.TYPE_TRAFFIC_JAM:
                showSettingsAlert(data, type);
                break;
            case AppConstants.TYPE_ACCIDENT:
                showSettingsAlert(data, type);
                break;
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
        alertDialog.setMessage("ban co muon chup anh khong");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        final double latitude = Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LATITUDE).trim());
        final double longtitude = Double.parseDouble(data.getmItemData().get(AppConstants.KEY_LONGTITUDE).trim());
        final LatLng latLng = new LatLng(latitude, longtitude);
        alertDialog.setPositiveButton("Co", new DialogInterface.OnClickListener() {
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
                    newPosts.setName(mUser.getName());
                    newPosts.setCreated_at(Utilities.currentDate());
                    intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    startActivityForResult(intentTakePhoto, AppConstants.REQUEST_TAKE_PHOTO);
                } else {
                    Toast.makeText(MainActivity.this, "Device does not support camera", Toast.LENGTH_LONG).show();

                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Khong", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mUtilities.isConnected()) {
                    progressDialog.show();
                    Posts posts = new Posts(mUtilities.getAddressFromLatLng(latLng), mUser.getName(), type, AppConstants.GOOD_RANK, latitude + "", longtitude + "", "", Utilities.currentDate());
                    mDatabase.child(AppConstants.POSTS).child(mUser.getUid()).setValue(posts);
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

    public void getAllFriends() {
        listUser = new ArrayList<>();
        listUser.clear();
        mDatabase.child(AppConstants.USER).child(user_uid).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mDatabase.child(AppConstants.USER).child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        UserTraffic userTraffic = new UserTraffic(name,avatar,status);
                        listUser.add(userTraffic);
                        adapter = new ListFriendAdapter(MainActivity.this, listUser);
                        lv_listfriend.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
    }
}
