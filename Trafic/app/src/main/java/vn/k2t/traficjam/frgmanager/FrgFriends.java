package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
         //   mDatabase = FirebaseDatabase.getInstance().getReference("trafficjam-edd7e");
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
        Toast.makeText(getActivity(),"onfriends",Toast.LENGTH_SHORT).show();

        list = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserTraffic result = dataSnapshot.getValue(UserTraffic.class);
                Toast.makeText(getActivity(),result+ "",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}
