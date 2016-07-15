package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.maps.MapFragMent;
import vn.k2t.traficjam.maps.MapManager;
import vn.k2t.traficjam.maps.MapsManager;
import vn.k2t.traficjam.model.ItemData;
import vn.k2t.traficjam.onclick.ItemClick;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by root on 7/15/16.
 */
public class FragGoogleMap extends FrgBase {
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

    private SupportMapFragment fragment;
    private GoogleMap mMap;
    public static FragGoogleMap f;
    public static Context mContext;
    public static ItemClick click;
    private Utilities utilities;
    private MapsManager mapManager;

    public FragGoogleMap() {
        super();
    }

    public static FragGoogleMap newInstance(Context context) {
        f= new FragGoogleMap();
        mContext=context;
        click= (ItemClick) mContext;
        return f;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        utilities = new Utilities(mContext);

        if (utilities.isConnected()) {
            rootView = inflater.inflate(R.layout.layout_google_map, container, false);
            ButterKnife.bind(this, rootView);
            initFloatingActionMenu(rootView);

        } else {
            rootView = inflater.inflate(R.layout.layout_google_map, container, false);
            return rootView;
        }
//        rootView = inflater.inflate(R.layout.frg_maps, container, false);
//        ButterKnife.bind(this, rootView);
//        setupFab();

        return rootView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        if (utilities.isConnected()){
            fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, fragment).commit();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (utilities.isConnected()&& mMap==null){
            mMap = fragment.getMap();
            mapManager= new MapsManager(mMap,mContext);
        }
    }

    private String type = new String();

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
                    click.selectedItem(data, AppConstants.TYPE_TRAFFIC_JAM);
                    menu.collapse();
                    break;
                case AppConstants.TYPE_ACCIDENT:
                    click.selectedItem(data, AppConstants.TYPE_ACCIDENT);
                    menu.collapse();
                    break;

            }
        } else if (button == actionSee) {
            switch (type) {
                case AppConstants.TYPE_TRAFFIC_JAM:
                    click.selectedItem(data, AppConstants.TYPE_SEE_TRAFFIC_JAM);
                    menu.collapse();
                    break;
                case AppConstants.TYPE_ACCIDENT:
                    click.selectedItem(data, AppConstants.TYPE_SEE_ACCIDENT);
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

    public void addMarker(String title, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

    }

    private void initFloatingActionMenu(View view) {

        menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                hide();
            }

            @Override
            public void onMenuCollapsed() {
                hide();
            }
        });
    }
//    private void setupFab() {
//
//
//        int sheetColor = getResources().getColor(R.color.background_card);
//        int fabColor = getResources().getColor(R.color.theme_accent);
//
//        // Create material sheet FAB
//        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);
//
//        // Set material sheet event listener
//        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
//            @Override
//            public void onShowSheet() {
//                // Save current status bar color
//                statusBarColor = getStatusBarColor();
//                // Set darker status bar color to match the dim overlay
//                setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//            }
//
//            @Override
//            public void onHideSheet() {
//                // Restore status bar color
//                setStatusBarColor(statusBarColor);
//            }
//        });
//
//        // Set material sheet item click listeners
//        trafficJam.setOnClickListener(this);
//        accident.setOnClickListener(this);
//
//    }
//
//    private int getStatusBarColor() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            return getActivity().getWindow().getStatusBarColor();
//        }
//        return 0;
//    }
//
//    private void setStatusBarColor(int color) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().getWindow().setStatusBarColor(color);
//        }
//    }

//    @Override
//    public void onClick(View v) {
//        ItemData data = new ItemData();
//        double latitude = MapFragMent.mMap.getMyLocation().getLatitude();
//        double longtitude = MapFragMent.mMap.getMyLocation().getLongitude();
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put(AppConstants.KEY_LATITUDE, latitude + "");
//        hashMap.put(AppConstants.KEY_LONGTITUDE, longtitude + "");
//        data.setmItemData(hashMap);
//        switch (v.getId()) {
//            case R.id.fab_sheet_item_traffic_jam:
//                //tickLocation(BitmapDescriptorFactory.HUE_RED);
//                click.selectedItem(data, AppConstants.TYPE_TRAFFIC_JAM);
//                materialSheetFab.hideSheet();
//                break;
//
//            case R.id.fab_sheet_item_accident:
//                //tickLocation(BitmapDescriptorFactory.HUE_GREEN);
//                click.selectedItem(data, AppConstants.TYPE_ACCIDENT);
//                materialSheetFab.hideSheet();
//                break;
//        }
//
//    }


    private void tickLocation(float a) {
        MarkerOptions markerOptions = new MarkerOptions();
        double latitude = MapFragMent.mMap.getMyLocation().getLatitude();
        double longtitude = MapFragMent.mMap.getMyLocation().getLongitude();
        LatLng latLng = new LatLng(latitude, longtitude);
        markerOptions.position(latLng);
        markerOptions.title(getAddressFromLatLng(latLng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(a));
        MapFragMent.mMap.addMarker(markerOptions);
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


}
