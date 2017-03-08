package cit.edu.paloma;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import cit.edu.paloma.adapters.FriendListAdapter;
import cit.edu.paloma.datamodals.User;


public class FindFriendsFragment extends Fragment {
    private View rootView;
    private ListView mSuggestedFriendsList;
    private FriendListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_friends, container, false);
        initViews();
        return rootView;
    }

    public void addSuggestedFriend(User friend) {
        
    }

    private void initViews() {
        mAdapter = new FriendListAdapter(getContext());

        mSuggestedFriendsList = (ListView) rootView.findViewById(R.id.suggested_friends_list);
        mSuggestedFriendsList.setAdapter(mAdapter);
    }
}
