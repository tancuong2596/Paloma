package cit.edu.paloma;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class FindFriendsFragment extends Fragment {
    private View rootView;
    private ListView mSuggestedFriendsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_friends, container, false);
        initViews();
        return rootView;
    }

    private void initViews() {
        mSuggestedFriendsList = (ListView) rootView.findViewById(R.id.suggested_friends_list);
    }
}
