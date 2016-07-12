package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gordonwong.materialsheetfab.MaterialSheetFab;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.adapter.ListFriendAdapter;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by chung on 7/11/16.
 */
public class FrgFriends extends FrgBase {
    private static final String TAG = "FrgFriends";
    private Utilities utilities;
    private static FrgFriends f;
    private static Context mContext;
    private ListFriendAdapter listFriendAdapter;
    private DatabaseReference mDatabase;
    private ArrayList<UserTraffic> list;
    private MaterialSheetFab materialSheetFab;
    @Bind(R.id.rv_listfriend)
    RecyclerView rv_listfriends;

    public static FrgFriends newInstance(Context context) {
        f = new FrgFriends();
        mContext = context;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        utilities = new Utilities(mContext);

        if (utilities.isConnected()) {
            rootView = inflater.inflate(R.layout.frg_friends, container, false);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            ButterKnife.bind(this, rootView);
            initView();
//            setupFab();
        } else {
            super.newInstance(mContext);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }

    private void initView() {

        list = new ArrayList<>();
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, dataSnapshot.getValue().toString());
//                String name = dataSnapshot.child("name").getValue().toString();
//                String avatar = dataSnapshot.child("avatar").getValue().toString();
                UserTraffic userTraffic = dataSnapshot.getValue(UserTraffic.class);
                Log.e(TAG, userTraffic.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
