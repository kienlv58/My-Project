package vn.k2t.traficjam.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;

/**
 * Created by chung on 7/11/16.
 */


public class ListFriendAdapter extends ArrayAdapter<UserTraffic> {

    private final ImageLoader imageLoader;
    private LayoutInflater inflater;
    int s;
    DatabaseReference mDatabase;
    ArrayList<UserTraffic> objects;
    SQLUser sqlUser;
    String uid;
    ProgressDialog progressDialog;


    public ListFriendAdapter(Context context, ArrayList<UserTraffic> objects, int s) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        this.s = s;
        this.objects = objects;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sqlUser = new SQLUser(getContext());
        uid = sqlUser.getUser().getUid();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (s == 1) {
            if (view == null) {
                viewHolder = new ViewHolder();

                view = inflater.inflate(R.layout.item_listfriends, null);
                viewHolder.layout1 = (RelativeLayout) view.findViewById(R.id.layout1);
                viewHolder.layout2 = (RelativeLayout) view.findViewById(R.id.layout2);
                viewHolder.tv_name_user = (TextView) view.findViewById(R.id.tv_name_user);
                viewHolder.tv_status_user = (TextView) view.findViewById(R.id.tv_status_user);
                viewHolder.iv_avatar_user = (ImageView) view.findViewById(R.id.iv_avatar_user);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.GONE);
            viewHolder.tv_name_user.setText(getItem(position).getName());
            int status = getItem(position).getStatus();
            if (status == 1) {
                viewHolder.tv_status_user.setText("online");
            } else {
                viewHolder.tv_status_user.setText("offline");
            }
            String imagestr = getItem(position).getAvatar();
            if (imagestr.contains("http")) {
                imageLoader.displayImage(imagestr, viewHolder.iv_avatar_user);
            } else if (imagestr.equals("") || imagestr.equals(" ")) {
                imageLoader.displayImage("drawable://" + R.drawable.bg_user_profile, viewHolder.iv_avatar_user);
            } else {
                viewHolder.iv_avatar_user.setImageBitmap(StringToBitMap(imagestr));
            }
        } else if (s == 2) {
            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.item_listfriends, null);
                viewHolder.layout1 = (RelativeLayout) view.findViewById(R.id.layout1);
                viewHolder.layout2 = (RelativeLayout) view.findViewById(R.id.layout2);
                viewHolder.tv_name_user = (TextView) view.findViewById(R.id.name_user);
                viewHolder.tv_email = (TextView) view.findViewById(R.id.email_user);
                viewHolder.iv_avatar_user = (ImageView) view.findViewById(R.id.avatar_user);
                viewHolder.btn_accept = (Button) view.findViewById(R.id.btn_accept);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.layout1.setVisibility(View.GONE);
            viewHolder.layout2.setVisibility(View.VISIBLE);
            viewHolder.tv_name_user.setText(getItem(position).getName());
            viewHolder.tv_email.setText(getItem(position).getEmail());
//            int status = getItem(position).getStatus();
//            if (status == 1) {
//                viewHolder.tv_status_user.setText("online");
//            } else {
//                viewHolder.tv_status_user.setText("offline");
//            }
            viewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDatabase.child(AppConstants.USER).child(uid).child("friends").child("friend_request").child(getItem(position).getUid()).removeValue();
                    mDatabase.child(AppConstants.USER).child(uid).child("friends").child("my_friends").child(getItem(position).getUid()).setValue(getItem(position).getUid());
                    mDatabase.child(AppConstants.USER).child(getItem(position).getUid()).child("friends").child("my_friends").child(uid).setValue(uid);
                    objects.remove(position);
                    notifyDataSetChanged();
                }
            });
            String imagestr = getItem(position).getAvatar();
            if (imagestr.contains("http")) {
                imageLoader.displayImage(imagestr, viewHolder.iv_avatar_user);
            } else if (imagestr.equals("") || imagestr.equals(" ")) {
                imageLoader.displayImage("drawable://" + R.drawable.bg_user_profile, viewHolder.iv_avatar_user);
            } else {
                viewHolder.iv_avatar_user.setImageBitmap(StringToBitMap(imagestr));
            }

        }
        return view;

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

    class ViewHolder {
        TextView tv_name_user, tv_status_user;
        ImageView iv_avatar_user;
        TextView tv_email;
        Button btn_accept;
        RelativeLayout layout1;
        RelativeLayout layout2;

    }


}
