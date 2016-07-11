package vn.k2t.traficjam.maps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;


/**
 * Created by root on 07/07/2016.
 */
public class MapManager implements GoogleMap.OnMyLocationChangeListener, LocationListener {
    private static final String TAG = "MapManager";
    private GoogleMap gMap;
    private Context mContext;
    private Marker myMarker;
    private LocationManager locationMgr;
    private Location mLocation;


    public MapManager(GoogleMap gMap, Context mContext) {
        this.gMap = gMap;
        this.mContext = mContext;
        initMap();
    }

    public void addMarker(String title, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        gMap.addMarker(markerOptions);


        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

    }

//    private GoogleMap.InfoWindowAdapter viewInfo = new GoogleMap.InfoWindowAdapter() {
//        @Override
//        public View getInfoWindow(Marker marker) {
//            return null;
//        }
//
//        @Override
//        public View getInfoContents(Marker marker) {
//            //marker.get
//            View view = View.inflate(mContext, R.layout.item_window, null);
//
//
//            return view;
//        }
//    };

    public void initMap() {
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        gMap.addMarker(new MarkerOptions().position(latLng));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Log.i("aaaa", "Latitude:" + latitude + ", Longitude:" + longitude);
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

    public void addLine(LatLng start, LatLng end) {
        PolygonOptions line = new PolygonOptions();
        line.add(start).add(end).fillColor(Color.BLUE).strokeColor(Color.BLUE).strokeWidth(5);
        gMap.addPolygon(line);
    }

    @Override
    public void onMyLocationChange(Location location) {
        mLocation = location;
        LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        if (myMarker == null) {
            //  myLocationName = getAddressName(mLocation);
            addMarker("My location", latLng);
            CameraPosition cameraPosition = new CameraPosition(latLng, 17, 0, 0);
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            myMarker.setPosition(latLng);
            //myMarker.setSnippet(myLocationName);

        }

    }

    public void checkGPSIsOn() {
        if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }


}
