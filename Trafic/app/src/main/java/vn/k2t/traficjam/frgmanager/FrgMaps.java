package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by root on 06/07/2016.
 */
public class FrgMaps extends FrgBase {
    private static Context mContext;
    private static FrgMaps f;
    private Utilities utilities;

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
        } else {
            super.newInstance("", mContext);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }
}
