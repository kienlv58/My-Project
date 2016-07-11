package vn.k2t.traficjam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ListFriendAdapter extends RecyclerView.Adapter<ListFriendAdapter.RecyclerViewHolder> {
    public static final String KEY_VIDEO = "key_video";
    public final ImageLoader imageLoader;
    private ArrayList<UserTraffic> list = new ArrayList<>();
    private Context context;

    public ListFriendAdapter(Context context, ArrayList<UserTraffic> list) {
        this.context = context;
        this.list = list;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_listfriends, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tv_name_user.setText(list.get(position).getName() + ":"+ list.get(position).getAvatar());
//        holder.tv_video_date.setText(s[0]);
//        holder.tv_video_time.setText(formatDate);
//        imageLoader.displayImage(list.get(position).getThumb(), holder.iv_thumb);
    }

    public ArrayList<UserTraffic> getList() {
        return list;
    }

    public void setList(ArrayList<UserTraffic> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name_user;
        ImageView iv_avatar_user;
        final Context context;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
            tv_name_user = (TextView) itemView.findViewById(R.id.tv_name_user);
            iv_avatar_user = (ImageView) itemView.findViewById(R.id.iv_avatar_user);
        }
        @Override
        public void onClick(View v) {

        }
    }


}
