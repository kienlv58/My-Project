package vn.k2t.traficjam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import vn.k2t.traficjam.R;

/**
 * Created by Admin on 4/04/2016.
 */
public class InfoAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;

    public InfoAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view=inflater.inflate(R.layout.inforwindow,null);
        TextView tvMylocation= (TextView) view.findViewById(R.id.tvMyLocation);
        TextView tvAddress= (TextView) view.findViewById(R.id.tvAddress);
        tvAddress.setText(marker.getSnippet());
        tvMylocation.setText(marker.getTitle());
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view=inflater.inflate(R.layout.inforwindow,null);
        TextView tvMylocation= (TextView) view.findViewById(R.id.tvMyLocation);
        TextView tvAddress= (TextView) view.findViewById(R.id.tvAddress);
        tvAddress.setText(marker.getSnippet());
        tvMylocation.setText(marker.getTitle());
        return null;
    }
}
