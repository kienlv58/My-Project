package vn.k2t.traficjam.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.CommonMethod;

/**
 * Created by root on 06/07/2016.
 */
public class ActivityUserProfile extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ActivityUserProfile";
    private static final int SELECT_IMAGE = 1;
    private Toolbar mToolbar;
    private Button profile_btn_update;
    CircleImageView avatar;
    TextView tvUserName, tvEmail;
    UserTraffic mUser;
    SQLUser sqlUser;
    private Dialog dialog;
    private String _uid, name;
    private DatabaseReference mDatabase;
    private UserTraffic userTraffic = null;
    private Bitmap bitmap;
    private CircleImageView dialog_image_update;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profile_btn_update = (Button) findViewById(R.id.profile_btn_update);
        profile_btn_update.setOnClickListener(this);
        mUser = MainActivity.mUser;
        avatar = (CircleImageView) findViewById(R.id.profile_image);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        if (mUser != null) {
            CommonMethod.getInstance().loadImage(mUser.getAvatar(), avatar);
            tvUserName.setText(mUser.getName());
            tvEmail.setText(mUser.getEmail());
            _uid = mUser.getUid();
            Log.e(TAG, _uid);
        }

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.WhiteColor), PorterDuff.Mode.SRC_ATOP);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }
        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                sqlUser = new SQLUser(this);
                sqlUser.deleteUser();
                Intent intent = new Intent();
                setResult(300, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_btn_update:
                dialog.show();
                break;
        }
    }

    public void initDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update_profile, null);
        final EditText dialog_edt_name = (EditText) view.findViewById(R.id.dialog_edt_name);
        final EditText dialog_edt_phone = (EditText) view.findViewById(R.id.dialog_edt_phone);
        Button dialog_btn_update = (Button) view.findViewById(R.id.dialog_btn_update);
        Button dialog_btn_cancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
        imageView = (ImageView) view.findViewById(R.id.test_bitmap);
        dialog_image_update = (CircleImageView) view.findViewById(R.id.dialog_image_update);
        dialog_image_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/pictures/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });
        if (mDatabase.child("user").child(_uid) != null) {
            mDatabase.child("user").child(_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dialog_edt_name.setText(dataSnapshot.child("name").getValue().toString());
                    dialog_edt_phone.setText(dataSnapshot.child("phone").getValue().toString());
//                dialog_image_update.setImageURI(Uri.parse(dataSnapshot.child("avatar").getValue().toString()));
                    CommonMethod.getInstance().loadImage(dataSnapshot.child("avatar").getValue().toString(), dialog_image_update);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            dialog_btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog_btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        dialog.setCancelable(true);
        dialog.setContentView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE && resultCode == ActivityUserProfile.RESULT_OK) {
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                Log.e("bitmap", bitmap.toString());
                stream.close();
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }
}
