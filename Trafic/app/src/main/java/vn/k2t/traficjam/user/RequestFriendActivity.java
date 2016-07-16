package vn.k2t.traficjam.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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
import vn.k2t.traficjam.model.Friends;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;

public class RequestFriendActivity extends AppCompatActivity {

    ArrayList<Friends> lisRequest;
    ListFriendAdapter adapter;
    ArrayList<UserTraffic> lisUser=new ArrayList<>();
    ListView listRQ;
    private static DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friend);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        lisRequest = MainActivity.listRequest;
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                getAllUser();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter = new ListFriendAdapter(getApplicationContext(),lisUser,2);
                listRQ = (ListView)findViewById(R.id.listRQ);
                listRQ.setAdapter(adapter);
                Intent intent =  new Intent();
                setResult(AppConstants.REQUEST_ADD_FRIENDS,intent);
            }
        }.execute();



    }
    public void getAllUser() {
        lisUser = new ArrayList<>();
        lisUser.clear();
        for (int i = 0; i < lisRequest.size();i++){
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
        Intent intent =  new Intent();
        setResult(AppConstants.REQUEST_ADD_FRIENDS,intent);
    }
}
