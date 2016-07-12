package vn.k2t.traficjam.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.model.Posts;

/**
 * Created by root on 12/07/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Posts> items = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public PostsAdapter(Context mContext, ArrayList<Posts> items) {
        this.mContext = mContext;
        this.items = items;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder holder, int position) {
        if (items.get(position).getImage() != "") {
            byte[] imageAsBytes = Base64.decode(items.get(position).getImage().getBytes(), Base64.DEFAULT);
            holder.ivAvatar.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        } else {
            holder.ivAvatar.setImageResource(R.mipmap.ic_launcher);
        }
        holder.tvName.setText(items.get(position).getName());
        holder.tvBody.setText(items.get(position).getTitle());
        holder.tvTime.setText(items.get(position).getCreated_at());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //@Bind(R.id.iv_avatar)
        CircleImageView ivAvatar;
        //@Bind(R.id.tv_name)
        TextView tvName;
       // @Bind(R.id.tv_body)
        TextView tvBody;
       // @Bind(R.id.tv_time)
        TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
           // ButterKnife.bind(this, itemView);
            ivAvatar= (CircleImageView) itemView.findViewById(R.id.iv_avatar);
            tvName= (TextView) itemView.findViewById(R.id.tv_name);
            tvBody= (TextView) itemView.findViewById(R.id.tv_body);
            tvTime= (TextView) itemView.findViewById(R.id.tv_time);
        }
    }


}
