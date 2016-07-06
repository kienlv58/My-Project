package vn.k2t.traficjam.frgmanager;

import android.content.Context;

import vn.k2t.traficjam.FrgBase;

/**
 * Created by root on 06/07/2016.
 */
public class FrgNews extends FrgBase {
    private static Context mContext;
    private static FrgNews f;

    public FrgNews() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static FrgNews newInstance(Context context) {
        f = new FrgNews();
        mContext = context;
        return f;
    }
}
