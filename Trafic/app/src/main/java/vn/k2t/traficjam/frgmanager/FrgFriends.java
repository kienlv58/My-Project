package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.adapter.ListFriendAdapter;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.Friends;
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
    private static DatabaseReference mDatabase;
    private ArrayList<UserTraffic> list;
    private SQLUser sqlUser;
    @Bind(R.id.lv_listfriend)
    ListView lv_listfriends;

    private String user_uid;

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
        } else {
            super.newInstance(mContext);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        user_uid = ((MainActivity) getActivity()).getUser_uid();
        insertFriends(user_uid);
        for (Friends f : sqlUser.getAllFriends()) {
            mDatabase.child(f.getFriend_uid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    list.add(new UserTraffic(name,avatar,email));
                    listFriendAdapter = new ListFriendAdapter(getActivity(), list);
                    lv_listfriends.setAdapter(listFriendAdapter);
                    listFriendAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void initView() {
        sqlUser = new SQLUser(getActivity());
        list = new ArrayList<>();

    }

    public void insertFriends(String _uid) {
        mDatabase.child(_uid).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String item = dataSnapshot.getValue().toString();
                long result = sqlUser.insertFriends(item);
                Log.e(TAG, "user: " + user_uid);
                Log.e(TAG, item);
                Log.e(TAG, result + " row");
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

    public ArrayList<UserTraffic> getAllUser() {
        final ArrayList<UserTraffic> userTraffics = new ArrayList<>();
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserTraffic userTraffic = dataSnapshot.getValue(UserTraffic.class);
                userTraffics.add(userTraffic);

                Log.e(TAG, userTraffics.size() + "");
                listFriendAdapter = new ListFriendAdapter(getActivity(), userTraffics);
                lv_listfriends.setAdapter(listFriendAdapter);
                listFriendAdapter.notifyDataSetChanged();

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
        return userTraffics;
    }

//    public ArrayList<UserTraffic> getListFriends(String uid) {
//        final ArrayList<UserTraffic> arrayList = new ArrayList<>();
//        for (int i = 0; i < getListUID(uid).size(); i++) {
//            mDatabase.child(getListUID(uid).get(i)).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    UserTraffic item = dataSnapshot.getValue(UserTraffic.class);
//                    arrayList.add(item);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//        return arrayList;
//    }
}
