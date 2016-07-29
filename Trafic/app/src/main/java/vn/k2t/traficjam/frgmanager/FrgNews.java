package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.adapter.PostsAdapter;
import vn.k2t.traficjam.model.Posts;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by root on 06/07/2016.
 */
public class FrgNews extends FrgBase {

    @Bind(R.id.rv_posts)
    RecyclerView rvPosts;
    RelativeLayout rl_loading;

    private static Context mContext;
    private static FrgNews f;
    private Utilities utilities;
    private PostsAdapter postsAdapter;
    private DatabaseReference mDatabase;
    private ArrayList<Posts> listPost = new ArrayList<>();

    public FrgNews() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static FrgNews newInstance(Context context) {
        f = new FrgNews();
        mContext = context;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        utilities = new Utilities(mContext);
        if (utilities.isConnected()) {
            Toast.makeText(getContext(),"hello",Toast.LENGTH_SHORT).show();
            rootView = inflater.inflate(R.layout.frg_news, container, false);
            ButterKnife.bind(this, rootView);
            mDatabase = FirebaseDatabase.getInstance().getReference().child(AppConstants.POSTS);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rl_loading = (RelativeLayout)rootView.findViewById(R.id.rl_loading);
            rl_loading.setVisibility(View.VISIBLE);
            rvPosts = (RecyclerView)rootView.findViewById(R.id.rv_posts);
            rvPosts.setLayoutManager(layoutManager);
            rvPosts.setHasFixedSize(true);
            postsAdapter = new PostsAdapter(getContext(), listPost,mDatabase);
            rvPosts.setAdapter(postsAdapter);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {

                    super.onPreExecute();

                }

                @Override
                protected Void doInBackground(Void... params) {
                    mDatabase.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Posts p = dataSnapshot.getValue(Posts.class);
                            listPost.add(p);
                            postsAdapter.notifyDataSetChanged();

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
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    rl_loading.setVisibility(View.GONE);
                }
            }.execute();


        } else

        {
            super.newInstance(mContext);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        return rootView;
    }

}

