package vn.k2t.traficjam.frgmanager;

import android.content.Context;

import vn.k2t.traficjam.FrgBase;

/**
 * Created by root on 06/07/2016.
 */
public class FrgMaps extends FrgBase {
    private static Context mContext;
    private static FrgMaps f;

    public FrgMaps() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static FrgMaps newInstance(Context context) {
        f = new FrgMaps();
        mContext = context;
        return f;
    }
}
