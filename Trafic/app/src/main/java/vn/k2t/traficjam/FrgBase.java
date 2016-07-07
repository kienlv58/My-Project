package vn.k2t.traficjam;

/**
 * Created by nguyennm on 5/11/16.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import vn.k2t.traficjam.untilitis.Utilities;


public class FrgBase extends Fragment {

    private static FrgBase f;
   // private static ItemData mItem;
    private static String mKey = new String();
    private static Context mContext;
    private Utilities utilities;
    public FrgBase() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static FrgBase newInstance(Context context) {
        f = new FrgBase();
       // mItem = item;
        mContext = context;
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view;
        utilities = new Utilities(mContext);
        view = inflater.inflate(R.layout.layout_network_status, container, false);
        RelativeLayout refresh = (RelativeLayout) view.findViewById(R.id.layout_refresh_id);
        ImageView imgrefresh = (ImageView) view.findViewById(R.id.image_refresh_id);
        imgrefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(utilities.isConnected())
                    addFragment();
            }
        });
        return view;
    }

    /**
     *
     * Replace Fragment
     */
    public void addFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

}