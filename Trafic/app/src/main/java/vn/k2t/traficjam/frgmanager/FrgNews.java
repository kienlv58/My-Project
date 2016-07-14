package vn.k2t.traficjam.frgmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.FrgBase;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.adapter.PostsAdapter;
import vn.k2t.traficjam.untilitis.AppConstants;
import vn.k2t.traficjam.untilitis.Utilities;

/**
 * Created by root on 06/07/2016.
 */
public class FrgNews extends FrgBase {

    @Bind(R.id.rv_posts)
    RecyclerView rvPosts;

    private static Context mContext;
    private static FrgNews f;
    private Utilities utilities;
    private PostsAdapter postsAdapter;
    private DatabaseReference mDatabase;

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
        // if (utilities.isConnected()) {
        rootView = inflater.inflate(R.layout.frg_news, container, false);
        ButterKnife.bind(this, rootView);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(AppConstants.POSTS);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setHasFixedSize(true);
//            mDatabase.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    ArrayList<Posts> items = new ArrayList<Posts>();
//                    Posts item = dataSnapshot.getValue(Posts.class);
//                    items.add(item);
//                    if (items != null) {
//                        postsAdapter = new PostsAdapter(getActivity(), items);
//                        rvPosts.setAdapter(postsAdapter);
//                        postsAdapter.notifyDataSetChanged();
//                    } else {
//
//                    }
//
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
//        } else {
//            super.newInstance(mContext);
//            return super.onCreateView(inflater, container, savedInstanceState);
//        }

        return rootView;
    }
}

