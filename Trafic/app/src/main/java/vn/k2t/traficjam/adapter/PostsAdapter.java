package vn.k2t.traficjam.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.model.Posts;
import vn.k2t.traficjam.untilitis.CommonMethod;

/**
 * Created by root on 12/07/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Posts> items = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private DatabaseReference mDatabase;

    public PostsAdapter(Context mContext, ArrayList<Posts> items,DatabaseReference mDatabase) {
        this.mContext = mContext;
        this.items = items;
        this.mDatabase = mDatabase;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostsAdapter.ViewHolder holder, final int position) {
        if (items.get(position).getAvatar().contains("http")) {
            CommonMethod.getInstance(mContext).loadImage(items.get(position).getAvatar(), holder.ivAvatar);
        } else if (items.get(position).getAvatar().isEmpty()) {
            holder.ivAvatar.setImageResource(R.drawable.ic_user_profile);
        } else {

            holder.ivAvatar.setImageBitmap(StringToBitMap(items.get(position).getAvatar()));
        }
        holder.tvName.setText(items.get(position).getName());
        holder.tvTitle.setText(items.get(position).getTitle());
        holder.tvTime.setText(items.get(position).getCreated_at());
        holder.tvlocation.setText(items.get(position).getLocation());
        holder.txtv_like.setText(items.get(position).getLike());
        holder.txtv_share.setText(items.get(position).getShare());
        holder.txtv_report.setText(items.get(position).getReport());
        if (!items.get(position).getImage().equals("")) {
            holder.img_status.setVisibility(View.VISIBLE);
            byte[] imageAsBytes = Base64.decode(items.get(position).getImage().getBytes(), Base64.DEFAULT);
            holder.img_status.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        } else {
            holder.img_status.setVisibility(View.GONE);
        }
        holder.img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int like = Integer.parseInt(items.get(position).getLike());
                like++;
                holder.txtv_like.setText(like+"");
                items.get(position).setLike(like+"");
                mDatabase.child(items.get(position).getUser_id()).child("like").setValue(like+"");

            }
        });
        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int share = Integer.parseInt(items.get(position).getShare());
                share++;
                holder.txtv_share.setText(share+"");
                items.get(position).setShare(share+"");
                mDatabase.child(items.get(position).getUser_id()).child("share").setValue(share+"");

            }
        });
        holder.img_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int report = Integer.parseInt(items.get(position).getReport());
                report++;
                holder.txtv_report.setText(report+"");
                items.get(position).setReport(report+"");
                mDatabase.child(items.get(position).getUser_id()).child("report").setValue(report+"");

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //@Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        //@Bind(R.id.tv_name)
        TextView tvName;
       // @Bind(R.id.tv_body)
        TextView tvTitle;
       // @Bind(R.id.tv_time)
        TextView tvTime;
        TextView tvlocation;
        ImageView img_status;
        TextView txtv_like;
        TextView txtv_report;
        TextView txtv_share;
        ImageView img_like;
        ImageView img_share;
        ImageView img_report;

        public ViewHolder(View itemView) {
            super(itemView);
           // ButterKnife.bind(this, itemView);
            ivAvatar= (ImageView) itemView.findViewById(R.id.avatar);
            tvName= (TextView) itemView.findViewById(R.id.txtv_name);
            tvTitle= (TextView) itemView.findViewById(R.id.txtv_status);
            tvTime= (TextView) itemView.findViewById(R.id.txtv_time);
            tvlocation = (TextView)itemView.findViewById(R.id.txtv_location);
            img_status = (ImageView)itemView.findViewById(R.id.img_status);
            txtv_like = (TextView)itemView.findViewById(R.id.txtv_like);
            txtv_report = (TextView)itemView.findViewById(R.id.txtv_report);
            txtv_share = (TextView)itemView.findViewById(R.id.txtv_share);
            img_like = (ImageView)itemView.findViewById(R.id.img_like);
            img_share = (ImageView)itemView.findViewById(R.id.img_share);
            img_report = (ImageView)itemView.findViewById(R.id.img_report);
        }
    }
    public static Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
