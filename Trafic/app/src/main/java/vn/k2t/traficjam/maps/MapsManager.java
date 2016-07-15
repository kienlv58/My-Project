package vn.k2t.traficjam.maps;

import android.content.Context;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.k2t.traficjam.adapter.Direction;
import vn.k2t.traficjam.adapter.InfoAdapter;

/**
 * Created by root on 7/15/16.
 */
public class MapsManager implements GoogleMap.OnMyLocationChangeListener {
    private GoogleMap mGoogleMap;
    private Location mLocation;
    private Marker mMarker;
    private Geocoder geocoder;
    private String myLocationName;
    private Context context;
    private InfoAdapter infoAdapter;
    private Polyline polyline;
    private Marker markerStart;
    private Marker markerEnd;
    private float distance;

    public MapsManager(GoogleMap mGoogleMap, Context context) {
        this.context = context;
        this.mGoogleMap = mGoogleMap;
        geocoder = new Geocoder(context, Locale.getDefault());
        initMaps();
        infoAdapter = new InfoAdapter(context);
        mGoogleMap.setInfoWindowAdapter(infoAdapter);
    }

    private void initMaps() {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMyLocationChange(Location location) {
        mLocation = location;
        LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        if (mMarker == null) {
            myLocationName = getAddressName(mLocation);
            mMarker = drawMarker(mLocation, BitmapDescriptorFactory.HUE_RED, "My location");
            CameraPosition cameraPosition = new CameraPosition(latLng, 17, 0, 0);
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            mMarker.setPosition(latLng);
            mMarker.setSnippet(myLocationName);

        }
    }

    private Marker drawMarker(Location location, float hue, String title) {
        MarkerOptions options = new MarkerOptions();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(hue));
        options.title(title);
        return mGoogleMap.addMarker(options);
    }

    private String getAddressName(Location location) {
        try {
            List<android.location.Address> list =
                    geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String locationName = list.get(0).getAddressLine(0);
            locationName += " - " + list.get(0).getAddressLine(1);
            locationName += " - " + list.get(0).getAddressLine(2);
            return locationName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void search(String diem1, String diem2) {
        Location location1 = getAddressLocation(diem1);
        Location location2 = getAddressLocation(diem2);
        if (location1 == null || location2 == null) {
            Toast.makeText(context, "Diem khong ton tai", Toast.LENGTH_LONG).show();
        } else {
            Log.e("Diem 1:", location1.getLatitude() + ":" + location1.getLongitude());
            Log.e("Diem 2:", location2.getLatitude() + ":" + location2.getLongitude());
            MyAsyctask myAsyctask = new MyAsyctask();
            myAsyctask.execute(location1, location2);
        }
    }

    private Location getAddressLocation(String name) {
        try {
            List<android.location.Address> list = geocoder.getFromLocationName(name, 1);
            double latitutde = list.get(0).getLatitude();
            double longitude = list.get(0).getLongitude();
            Location location = new Location("");
            location.setLatitude(latitutde);
            location.setLongitude(longitude);
            return location;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    class MyAsyctask extends AsyncTask<Location, Void, ArrayList<LatLng>> {

        @Override
        protected ArrayList<LatLng> doInBackground(Location... params) {
            Direction direction = new Direction();
            Document document = direction.getDocument(params[0], params[1]);
            ArrayList<LatLng> arrPoint = direction.getDirection(document);
            distance = direction.getDistanceValue(document);
            return arrPoint;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> latLngs) {
            super.onPostExecute(latLngs);
            PolylineOptions options = new PolylineOptions();
            options.width(10);
            options.color(Color.RED);
            options.addAll(latLngs);
            if (polyline != null) {
                polyline.remove();
            }
            if (markerStart != null && markerEnd != null) {
                markerEnd.remove();
                markerStart.remove();
            }
            if (latLngs.size() >= 2) {
                Location locationStart = new Location("");
                locationStart.setLatitude(latLngs.get(0).latitude);
                locationStart.setLongitude(latLngs.get(0).longitude);
                String nameStart = getAddressName(locationStart);
                Location locationEnd = new Location("");
                locationEnd.setLatitude(latLngs.get(latLngs.size() - 1).latitude);
                locationEnd.setLongitude(latLngs.get(latLngs.size() - 1).longitude);
                String nameEnd = getAddressName(locationEnd);
                markerStart = drawMarker(locationStart, BitmapDescriptorFactory.HUE_GREEN, "Point start(" + (distance / 1000) + "km)");
                markerEnd = drawMarker(locationEnd, BitmapDescriptorFactory.HUE_GREEN, "Point End(" + (distance / 1000) + "km)");
                markerStart.setSnippet(nameStart);
                markerEnd.setSnippet(nameEnd);
            }
            polyline = mGoogleMap.addPolyline(options);
        }
    }
}