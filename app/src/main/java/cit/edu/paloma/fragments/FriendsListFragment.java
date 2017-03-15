package cit.edu.paloma.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cit.edu.paloma.activities.MainActivity;
import cit.edu.paloma.R;
import cit.edu.paloma.adapters.FriendListAdapter;

public class FriendsListFragment extends Fragment {
    public static final int CREATE_NEW_USER_WITH_INFO_RC = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mFriendList;
    private FriendListAdapter mFriendListAdapter;
    private View mViewRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mFriendList = (ListView) mViewRoot.findViewById(R.id.friends_list);
        mFriendListAdapter = new FriendListAdapter(getContext(), mFriendList);
        mFriendList.setAdapter(mFriendListAdapter);

        return mViewRoot;
    }



}
