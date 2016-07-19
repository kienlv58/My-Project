package vn.k2t.traficjam.maps;

/**
 * Created by root on 07/07/2016.
 */

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import vn.k2t.traficjam.R;
import vn.k2t.traficjam.onclick.ItemClick;
import vn.k2t.traficjam.untilitis.AppConstants;

/**
 * Created by Paul on 8/11/15.
 */
public class MapFragMent extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    public static GoogleMap mMap;


    private static ItemClick connected;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        connected = (ItemClick) getActivity();
        mMap = getMap();
        initListeners();
    }
    private void initListeners() {

        getMap().setOnMarkerClickListener(this);
        // getMap().setOnMapLongClickListener(this);
        // getMap().setOnInfoWindowClickListener(this);
        getMap().setOnMapClickListener(this);
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
        connected.selectedItem(mCurrentLocation, AppConstants.TYPE_CONNETCED);

    }

    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), getActivity().getString(R.string.can_not_get_location_of_you), Toast.LENGTH_LONG).show();
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
//        to = new LatLng(latLng.latitude, latLng.longitude);
//        Routing routing = new Routing.Builder()
//                .travelMode(AbstractRouting.TravelMode.DRIVING)
//                .withListener(this)
//                .alternativeRoutes(true)
//                .waypoints(from, to)
//                .build();
//        routing.execute();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    }
}
