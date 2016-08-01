package vn.k2t.traficjam.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.k2t.traficjam.MainActivity;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.adapter.ListFriendAdapter;
import vn.k2t.traficjam.frgmanager.FrgFriends;
import vn.k2t.traficjam.frgmanager.FrgMyFriends;
import vn.k2t.traficjam.model.Friends;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;

public class RequestFriendActivity extends AppCompatActivity {

    ArrayList<Friends> lisRequest;
    ListFriendAdapter adapter;
    ArrayList<UserTraffic> lisUser = new ArrayList<>();
    ListView listRQ;
    private static DatabaseReference mDatabase;
    TextView txtv_noData;
    Button findFiend;
    boolean layout1;
    FragmentTransaction ft1;
    Fragment frag;
    FrameLayout fragment_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_friend);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        lisRequest = MainActivity.listRequest;
        txtv_noData = (TextView) findViewById(R.id.txtv_nodata);
        findFiend = (Button) findViewById(R.id.btn_find);
        fragment_container = (FrameLayout)findViewById(R.id.fragment_container);
        layout1 = true;


        findFiend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_container.setVisibility(View.VISIBLE);
                FragmentManager fm1 = RequestFriendActivity.this
                        .getSupportFragmentManager();
                ft1 = fm1.beginTransaction();
                 frag = new FrgFriends();
                ft1.replace(R.id.fragment_container, frag);
                ft1.commit();
                layout1 = false;
            }
        });


        if (lisUser.size() == 0) {
            txtv_noData.setVisibility(View.VISIBLE);
        } else {
            txtv_noData.setVisibility(View.GONE);
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                getAllUser();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (lisUser.size() == 0) {
                    txtv_noData.setVisibility(View.VISIBLE);
                } else {
                    txtv_noData.setVisibility(View.GONE);
                }
                adapter = new ListFriendAdapter(getApplicationContext(), lisUser, 2);
                listRQ = (ListView) findViewById(R.id.listRQ);
                listRQ.setAdapter(adapter);
                Intent intent = new Intent();
                setResult(AppConstants.REQUEST_ADD_FRIENDS, intent);
            }
        }.execute();


    }

    public void getAllUser() {
        lisUser = new ArrayList<>();
        lisUser.clear();
        for (int i = 0; i < lisRequest.size(); i++) {
            mDatabase.child(AppConstants.USER).child(lisRequest.get(i).getFriend_uid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserTraffic userTraffic = dataSnapshot.getValue(UserTraffic.class);
                    lisUser.add(userTraffic);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (layout1 == true){
            Intent intent = new Intent();
            setResult(AppConstants.REQUEST_ADD_FRIENDS, intent);
        }else {
           fragment_container.setVisibility(View.GONE);
            layout1 = true;
        }
    }
}
