package cit.edu.paloma.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cit.edu.paloma.MainActivity;
import cit.edu.paloma.R;
import cit.edu.paloma.adapters.FriendListAdapter;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

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


    public void updateFriendsList(Map<String, Object> friends, Map<String, Object> invites) {
        if (friends == null) {
            friends = Collections.emptyMap();
        }

        if (invites == null) {
            invites = Collections.emptyMap();
        }

        final HashSet<String> acceptedFriendsId = new HashSet<>();
        for (Map.Entry<String, Object> friend : friends.entrySet()) {
            String friendId = friend.getKey();
            String friendStatus = (String) friend.getValue();
            if (friendStatus.equalsIgnoreCase(MainActivity.FRIEND_ACCEPTED)) {
                acceptedFriendsId.add(friendId);
            }
        }

        final HashSet<String> invitedFriendsId = new HashSet<>();
        for (Map.Entry<String, Object> invite : invites.entrySet()) {
            String inviteId = invite.getKey();
            Boolean inviteStatus = (Boolean) invite.getValue();
            if (inviteStatus) {
                invitedFriendsId.add(inviteId);
            }
        }

        final ArrayList<User> updatedFriends = new ArrayList<>();
        final ArrayList<User> updatedInvites = new ArrayList<>();

        FirebaseUtils
                .getUsersRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (acceptedFriendsId.contains(user.getUserId())) {
                                updatedFriends.add(user);
                            }

                            if (invitedFriendsId.contains(user.getUserId())) {
                                updatedInvites.add(user);
                            }
                            Log.v(TAG, user.toString());
                        }

                        Log.v(TAG, "updatedFriends = " + updatedFriends.toString());
                        Log.v(TAG, "updatedInvites = " + updatedInvites.toString());
                        mFriendListAdapter.updateUsers(updatedFriends, updatedInvites);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
