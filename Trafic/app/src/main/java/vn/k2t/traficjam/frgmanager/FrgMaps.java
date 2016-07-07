package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.maps.MapManager;
import vn.k2t.traficjam.mylocation.GPSTracker;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by root on 06/07/2016.
 */
public class FrgMaps extends FrgBase implements OnMapReadyCallback {
    private static Context mContext;
    private static FrgMaps f;
    private Utilities utilities;
    private MapManager mapMgr;
    private GPSTracker mGPS;

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
            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps));
            mapFragment.getMapAsync(this);

        } else {
            super.newInstance(mContext);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapMgr = new MapManager(googleMap, getActivity());
        // mapMgr.addMarker("Ho Guom", new LatLng(21.029435, 105.851846));

    }
}
