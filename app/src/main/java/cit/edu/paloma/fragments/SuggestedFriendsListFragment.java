package cit.edu.paloma.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cit.edu.paloma.R;
import cit.edu.paloma.adapters.SuggestedFriendsListAdapter;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;


public class SuggestedFriendsListFragment extends Fragment {
    private View rootView;
    private ListView mSuggestedFriendsList;
    private SuggestedFriendsListAdapter mAdapter;
    private TextView mNoResultText;
    private TextView mSearchingText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_suggested_friends, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        mAdapter = new SuggestedFriendsListAdapter(getContext());
        mAdapter.initializeMemberSet();

        mSuggestedFriendsList = (ListView) rootView.findViewById(R.id.suggested_friends_list);
        mSuggestedFriendsList.setAdapter(mAdapter);

        mNoResultText = (TextView) rootView.findViewById(R.id.no_result_text);

        mSearchingText = (TextView) rootView.findViewById(R.id.searching_text);
    }

    public ArrayList<Object[]> getSelectedMembers() {
        return mAdapter.getMembers();
    }

    public void showProgressBar(boolean b) {
        if (b) {
            mSearchingText.setVisibility(View.VISIBLE);
            mNoResultText.setVisibility(View.GONE);
        } else {
            mSearchingText.setVisibility(View.GONE);
            if (mAdapter.isEmpty()) {
                mNoResultText.setVisibility(View.VISIBLE);
            } else {
                mNoResultText.setVisibility(View.GONE);
            }
        }
    }

    public void findUsersWithPattern(final String pattern) {
        if (mSearchingText.getVisibility() == View.VISIBLE) {
            return;
        }

        showProgressBar(true);
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        final FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseUtils
                .getUsersRef()
                .orderByChild("email")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            String email = user.getEmail().toLowerCase();

                            if (email.contains(pattern)) {
                                if (!email.equalsIgnoreCase(firebaseCurrentUser.getEmail())) {
                                    mAdapter.add(new Object[]{snapshot.getRef(), user});
                                }
                            }

                        }

                        mAdapter.notifyDataSetChanged();
                        showProgressBar(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mAdapter.clear();
                        mAdapter.notifyDataSetChanged();
                        showProgressBar(false);
                    }
                });
    }
}
