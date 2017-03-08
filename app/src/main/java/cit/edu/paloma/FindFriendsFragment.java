package cit.edu.paloma;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import cit.edu.paloma.adapters.SuggestedFriendListAdapter;
import cit.edu.paloma.datamodals.User;


public class FindFriendsFragment extends Fragment {
    private View rootView;
    private ListView mSuggestedFriendsList;
    private SuggestedFriendListAdapter mAdapter;
    private TextView mNoResultText;
    private TextView mSearchingText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_friends, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        mAdapter = new SuggestedFriendListAdapter(getContext());

        mSuggestedFriendsList = (ListView) rootView.findViewById(R.id.suggested_friends_list);
        mSuggestedFriendsList.setAdapter(mAdapter);

        mNoResultText = (TextView) rootView.findViewById(R.id.no_result_text);

        mSearchingText = (TextView) rootView.findViewById(R.id.searching_text);
    }

    public void setListOfUsers(@Nullable ArrayList<User> listOfUsers) {
        if (listOfUsers == null) {
            listOfUsers = (ArrayList<User>) Collections.<User>emptyList();
        }

        mAdapter = new SuggestedFriendListAdapter(getContext());
        mAdapter.addAll(listOfUsers);
        mSuggestedFriendsList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if (listOfUsers.isEmpty()) {
            mNoResultText.setVisibility(View.VISIBLE);
        } else {
            mNoResultText.setVisibility(View.GONE);
        }
    }

    public void showProgressBar(boolean b) {
        if (b) {
            mSearchingText.setVisibility(View.VISIBLE);
        } else {
            mSearchingText.setVisibility(View.GONE);
        }
    }
}
