package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.libs.Fab;
import vn.k2t.traficjam.maps.MapFragMent;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by root on 06/07/2016.
 */
public class FrgMaps extends FrgBase implements View.OnClickListener {


    @Bind(R.id.fab)
    Fab fab;
    @Bind(R.id.fab_sheet)
    View sheetView;
    @Bind(R.id.overlay)
    View overlay;
    @Bind(R.id.fab_sheet_item_traffic_jam)
    TextView trafficJam;
    @Bind(R.id.fab_sheet_item_accident)
    TextView accident;
    GoogleMap mMap;
    private static Context mContext;
    private static FrgMaps f;
    private Utilities utilities;
    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;

    public FrgMaps() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static FrgMaps newInstance(Context context) {
        f = new FrgMaps();
        mContext = context;

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        utilities = new Utilities(mContext);

        if (utilities.isConnected()) {
            rootView = inflater.inflate(R.layout.frg_maps, container, false);
            ButterKnife.bind(this, rootView);

            setupFab();
        } else {
            super.newInstance(mContext);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }

    public void addMarker(String title, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

    }

    private void setupFab() {


        int sheetColor = getResources().getColor(R.color.background_card);
        int fabColor = getResources().getColor(R.color.theme_accent);

        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });

        // Set material sheet item click listeners
        trafficJam.setOnClickListener(this);
        accident.setOnClickListener(this);

    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getActivity().getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_sheet_item_traffic_jam:
                if (MapFragMent.mMap != null) {
                    double latitude = MapFragMent.mMap.getMyLocation().getLatitude();
                    double longitude = MapFragMent.mMap.getMyLocation().getLongitude();
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latlng = new LatLng(latitude, longitude);
                    markerOptions.position(latlng);
                    markerOptions.title(getAddressFromLatLng(latlng));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //MapFragMent.mMap.clear();
                    MapFragMent.mMap.addMarker(markerOptions);

                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.can_not_get_location_of_you), Toast.LENGTH_LONG).show();
                }
                materialSheetFab.hideSheet();
                break;
            case R.id.fab_sheet_item_accident:
                materialSheetFab.hideSheet();
                break;
        }

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
