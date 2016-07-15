package vn.k2t.traficjam.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.frgmanager.FrgFriends;
import vn.k2t.traficjam.model.Friends;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.CommonMethod;

/**
 * Created by root on 06/07/2016.
 */
public class ActivityUserProfile extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ActivityUserProfile";
    private static final int SELECT_IMAGE = 1;
    private Toolbar mToolbar;
    private Button profile_btn_update, profile_btn_add, profile_btn_wait;
    CircleImageView avatar;
    TextView tvUserName, tvEmail;
    private UserTraffic mUser;
    SQLUser sqlUser;
    private Dialog dialog;
    private String friend_uid, user_uid;
    private DatabaseReference mDatabase;
    private UserTraffic userTraffic = null;
    private Bitmap bitmap;
    private CircleImageView dialog_image_update;
    private ImageView imageView;
    private String base64Image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sqlUser = new SQLUser(this);
        profile_btn_update = (Button) findViewById(R.id.profile_btn_update);
        profile_btn_update.setOnClickListener(this);
        profile_btn_add = (Button) findViewById(R.id.profile_btn_add);
        profile_btn_add.setOnClickListener(this);
        profile_btn_wait = (Button) findViewById(R.id.profile_btn_wait);
        profile_btn_wait.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            friend_uid = bundle.getString(FrgFriends.KEY_FRIEND_UID);
            user_uid = bundle.getString(FrgFriends.KEY_USER_UID);
            profile_btn_update.setEnabled(false);
            profile_btn_add.setEnabled(true);
            profile_btn_wait.setEnabled(false);
        } else {
            user_uid = sqlUser.getUser().getUid();
            profile_btn_update.setEnabled(true);
            profile_btn_add.setEnabled(false);
            profile_btn_wait.setEnabled(false);
        }
        avatar = (CircleImageView) findViewById(R.id.profile_image);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        if (friend_uid != null) {
            mDatabase.child(AppConstants.USER).child(friend_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvUserName.setText(dataSnapshot.child("name").getValue().toString());
                    tvEmail.setText(dataSnapshot.child("email").getValue().toString());
                    String imagestr = dataSnapshot.child("avatar").getValue().toString();
                    if (imagestr.contains("http") || imagestr.equals("") || imagestr.equals(" ")) {
                        CommonMethod.getInstance().loadImage(imagestr, avatar);
                    } else {
                        avatar.setImageBitmap(StringToBitMap(imagestr));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
            case R.id.profile_btn_add:
                addFriend();
                break;
            case R.id.profile_btn_wait:
                cancelRequest();
                break;
        }
    }

    private void cancelRequest() {
//        mDatabase.child(AppConstants.USER).child(friend_uid).child("friend_request").child(friend_uid).removeValue(new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                Toast.makeText(ActivityUserProfile.this, "canceler request", Toast.LENGTH_SHORT).show();
//            }
//        });
        mDatabase.child(AppConstants.USER).child(friend_uid).child("friends").child("friend_request").child(user_uid).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(ActivityUserProfile.this, "canceler request", Toast.LENGTH_SHORT).show();
            }
        });
        profile_btn_add.setEnabled(true);
        profile_btn_wait.setEnabled(false);
    }

    private void addFriend() {
//        Friends f1 = new Friends(friend_uid, 0, "send");
//        mDatabase.child(AppConstants.USER).child(user_uid).child("friend_request").child(_uid).setValue(f1);
        Friends f2 = new Friends(user_uid, 0, "get");
        mDatabase.child(AppConstants.USER).child(friend_uid).child("friends").child("friend_request").child(user_uid).setValue(f2);
        Toast.makeText(this, "sender request", Toast.LENGTH_SHORT).show();
        profile_btn_add.setEnabled(false);
        profile_btn_wait.setEnabled(true);

    }

    public void initDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update_profile, null);
        final EditText dialog_edt_name = (EditText) view.findViewById(R.id.dialog_edt_name);
        final EditText dialog_edt_phone = (EditText) view.findViewById(R.id.dialog_edt_phone);
        Button dialog_btn_update = (Button) view.findViewById(R.id.dialog_btn_update);
        Button dialog_btn_cancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
        dialog_image_update = (CircleImageView) view.findViewById(R.id.dialog_image_update);
        dialog_image_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takephoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(takephoto, SELECT_IMAGE);
            }
        });
        try {
            mDatabase.child("user").child(user_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        dialog_edt_name.setText(dataSnapshot.child("name").getValue().toString());
//                        if (dataSnapshot.child("phone") != null) {
//                            dialog_edt_phone.setText(dataSnapshot.child("phone").getValue().toString());
//                        }
                        String imagestr = dataSnapshot.child("avatar").getValue().toString();
                        if (imagestr.contains("http") || imagestr.equals("") || imagestr.equals(" ")) {
                            CommonMethod.getInstance().loadImage(imagestr, dialog_image_update);
                        } else {
                            dialog_image_update.setImageBitmap(StringToBitMap(imagestr));
                        }
                    }
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
                    if (bitmap != null) {
                        mDatabase.child(AppConstants.USER).child(user_uid).child("avatar").setValue(base64Image);
                    }
                    mDatabase.child(AppConstants.USER).child(user_uid).child("name").setValue(dialog_edt_name.getText().toString());
                    mDatabase.child(AppConstants.USER).child(user_uid).child("phone").setValue(dialog_edt_phone.getText().toString());
                    Toast.makeText(ActivityUserProfile.this, "update success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            FirebaseAuth.getInstance().signOut();
            sqlUser.deleteUser();
            Intent intent = new Intent();
            setResult(300, intent);
            finish();
        }


        dialog.setCancelable(false);
        dialog.setContentView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        if (requestCode == 1 && resultCode == ActivityUserProfile.RESULT_OK && data != null) {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS},
                        1);
            } else {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeFile(picturePath, options);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                dialog_image_update.setImageBitmap(bitmap);
            }


        }
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
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
