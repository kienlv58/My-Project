package vn.k2t.traficjam;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.adapter.TabAdapter;
import vn.k2t.traficjam.maps.MapsActivity;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.user.ActivityUserProfile;
import vn.k2t.traficjam.user.LoginUserActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    ImageView imgUser;

    private FragmentManager manager;
    private TabAdapter tabAdapter;

    //firebase
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    public static UserTraffic mUser;

    @Override
    protected void onStart() {
        super.onStart();
        getUserFirebaase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();

        imgUser = (ImageView)findViewById(R.id.imageView);
        if (mUser != null){
        imgUser.setImageURI(Uri.parse(mUser.getAvatar()));
        }
        initObject();



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
        setTabFragment();
    }

    private void initObject() {

        imgUser.setOnClickListener(this);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        switch (id){
            case R.id.imageView:
                if (mUser!=null){
                    startActivity(new Intent(this, ActivityUserProfile.class));
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LoginUserActivity.class);
                    startActivity(intent);
                }

        }

    }
    public void getUserFirebaase(){
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    mUser = new UserTraffic();
                    mUser.setUid(firebaseUser.getUid());
                    mUser.setName(firebaseUser.getDisplayName());
                    mUser.setEmail(firebaseUser.getEmail());
                    mUser.setAvatar(String.valueOf(firebaseUser.getPhotoUrl()));
                    mUser.setUidProvider(firebaseUser.getProviderId());

                }
                else {
                    Intent intent = new Intent(MainActivity.this, LoginUserActivity.class);
                    startActivity(intent);
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserFirebaase();
    }
}
