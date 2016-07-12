package vn.k2t.traficjam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import vn.k2t.traficjam.R;
import vn.k2t.traficjam.model.UserTraffic;

/**
 * Created by chung on 7/11/16.
 */


public class ListFriendAdapter extends ArrayAdapter<UserTraffic> {

    private final ImageLoader imageLoader;
    private LayoutInflater inflater;


    public ListFriendAdapter(Context context, ArrayList<UserTraffic> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_listfriends, null);
            viewHolder.tv_name_user = (TextView) view.findViewById(R.id.tv_name_user);
            viewHolder.iv_avatar_user = (ImageView) view.findViewById(R.id.iv_avatar_user);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_name_user.setText(getItem(position).getName());
        imageLoader.displayImage(getItem(position).getAvatar(), viewHolder.iv_avatar_user);
        return view;

    }
    class ViewHolder {
        TextView tv_name_user;
        ImageView iv_avatar_user;
    }


}
