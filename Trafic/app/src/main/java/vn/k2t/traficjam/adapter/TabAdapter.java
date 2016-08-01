package vn.k2t.traficjam.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vn.k2t.traficjam.frgmanager.FragGoogleMap;
import vn.k2t.traficjam.frgmanager.FrgFriends;
import vn.k2t.traficjam.frgmanager.FrgMaps;
import vn.k2t.traficjam.frgmanager.FrgMyFriends;
import vn.k2t.traficjam.frgmanager.FrgNews;

/**
 * Created by nguyennm on 5/11/16.
 */
public class TabAdapter extends FragmentPagerAdapter {
    private static final String[] TYPES = new String[]{ "NEWS", "FRIENDS"};

    ///private ItemData item;
    private Context mContext;
    private Fragment frg = new Fragment();
    private FragmentManager manager;

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        this.manager = fm;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                frg = FrgNews.newInstance(mContext);
                break;
            case 1:
                frg = FrgMyFriends.newInstance(mContext);
                break;
            default:
                frg = FrgNews.newInstance(mContext);

        }
        return frg;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TYPES[position].toUpperCase();
    }

    @Override
    public int getCount() {
        return TYPES.length;
    }
}
