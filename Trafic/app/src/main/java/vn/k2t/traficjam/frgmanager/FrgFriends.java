package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.adapter.ListFriendAdapter;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.Utilities;
import vn.k2t.traficjam.user.ActivityUserProfile;

/**
 * Created by chung on 7/11/16.
 */
public class FrgFriends extends FrgBase implements TextWatcher, AdapterView.OnItemClickListener {
    private static final String TAG = "FrgFriends";
    public static final String KEY_FRIEND_UID = "key_friend_uid";
    public static final String KEY_USER_UID = "key_user_uid";
    private Utilities utilities;
    private static FrgFriends f;
    private static Context mContext;
    private ListFriendAdapter listFriendAdapter;
    private static DatabaseReference mDatabase;
    private ArrayList<UserTraffic> list;
    private SQLUser sqlUser;
    @Bind(R.id.lv_listSearch)
    ListView lv_listSearch;
    @Bind(R.id.edt_search)
    EditText edt_search;
    @Bind(R.id.iv_search)
    ImageView iv_search;

    private String user_uid;
    private ArrayList<UserTraffic> listData;

    public FrgFriends() {
        super();
        // TODO Auto-generated constructor stub
    }

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
            sqlUser = new SQLUser(getActivity());
            user_uid = sqlUser.getUser().getUid();
            if (user_uid !=  null){

                getAllUser();
            }
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



    }

    private void initView() {
        list = new ArrayList<>();
        edt_search.addTextChangedListener(this);

    }

//    public void insertFriends(String _uid) {
//        mDatabase.child(_uid).child("friends").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                String item = dataSnapshot.getValue().toString();
//                long result = sqlUser.insertFriends(item);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void getAllUser() {
        list.clear();
        mDatabase.child(AppConstants.USER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserTraffic userTraffic = dataSnapshot.getValue(UserTraffic.class);
                list.add(userTraffic);

//                Log.e(TAG, list.size() + "");
//                listFriendAdapter = new ListFriendAdapter(getActivity(), list);
//                lv_listfriends.setAdapter(listFriendAdapter);
//                listFriendAdapter.notifyDataSetChanged();

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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.e("beforetextchange", s + "");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        listData = new ArrayList<>();
        if (searchWithName(s + "") != null) {
            listData.addAll(searchWithName(s + ""));
            listFriendAdapter = new ListFriendAdapter(getActivity(), listData,1);
        } else if (searchWithSdt(s + "") != null) {
            listData.addAll(searchWithSdt(s + ""));
            listFriendAdapter = new ListFriendAdapter(getActivity(), listData,1);
        } else if (searchWithEmail(s + "") != null) {
            listData.addAll(searchWithEmail(s + ""));
            listFriendAdapter = new ListFriendAdapter(getActivity(), listData,1);
        }

        lv_listSearch.setAdapter(listFriendAdapter);
        listFriendAdapter.notifyDataSetChanged();
        lv_listSearch.setOnItemClickListener(this);

    }

    @Override
    public void afterTextChanged(Editable s) {
        edt_search.clearFocus();
    }

    public ArrayList<UserTraffic> searchWithName(String newText) {
        ArrayList<UserTraffic> listSearch = new ArrayList<UserTraffic>();
        for (UserTraffic mItem : list) {
            if (mItem.getName().toLowerCase().contains(newText.toLowerCase())) {
                listSearch.add(mItem);
            }
        }
        return listSearch;
    }

    public ArrayList<UserTraffic> searchWithSdt(String phone) {
        ArrayList<UserTraffic> listSearch = new ArrayList<UserTraffic>();
        for (UserTraffic mItem : list) {
            if (mItem.getPhone().equals(phone)) {
                listSearch.add(mItem);
            }
        }
        return listSearch;
    }

    public ArrayList<UserTraffic> searchWithEmail(String email) {
        ArrayList<UserTraffic> listSearch = new ArrayList<UserTraffic>();
        for (UserTraffic mItem : list) {
            if (mItem.getEmail().equals(email)) {
                listSearch.add(mItem);
            }
        }
        return listSearch;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String friend_uid = listData.get(position).getUid();
        Intent intent = new Intent(getActivity(), ActivityUserProfile.class);
        intent.putExtra(KEY_FRIEND_UID, friend_uid);
        intent.putExtra(KEY_USER_UID, user_uid);
        startActivity(intent);
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
