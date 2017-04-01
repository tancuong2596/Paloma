package cit.edu.paloma.fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cit.edu.paloma.activities.ChatActivity;
import cit.edu.paloma.R;
import cit.edu.paloma.activities.MainActivity;
import cit.edu.paloma.adapters.FriendsListAdapter;
import cit.edu.paloma.datamodals.ChatGroup;
import cit.edu.paloma.datamodals.User;

public class FriendsListFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final int CREATE_NEW_USER_WITH_INFO_RC = 0;
    private static final String TAG = FriendsListFragment.class.getSimpleName();

    private ListView mFriendList;
    private FriendsListAdapter mFriendsListAdapter;
    private View mViewRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mFriendList = (ListView) mViewRoot.findViewById(R.id.friends_list);
        mFriendsListAdapter = new FriendsListAdapter(getContext(), mFriendList);
        mFriendList.setAdapter(mFriendsListAdapter);
        mFriendList.setOnItemClickListener(this);

        return mViewRoot;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();

        ChatGroup selectedChatGroup = mFriendsListAdapter.getItem(position);
        User currentUser = ((MainActivity) getActivity()).getCurrentUser();

        bundle.putString(ChatActivity.PARAM_ACTION_BAR_TITLE, getGroupName(view));
        bundle.putString(ChatActivity.PARAM_GROUP_CHAT_ID, selectedChatGroup.getGroupId());
        bundle.putString(ChatActivity.PARAM_GROUP_CHAT_NAME, selectedChatGroup.getGroupName());
        bundle.putString(ChatActivity.PARAM_CURRENT_USER_ID, currentUser.getUserId());
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private String getGroupName(View view) {
        TextView mainLeftInfo = (TextView) view.findViewById(R.id.usr_main_left_info_text);
        TextView tripleMainText = (TextView) view.findViewById(R.id.triple_main_text);
        if (mainLeftInfo == null) {
            return tripleMainText.getText().toString();
        } else {
            return mainLeftInfo.getText().toString();
        }
    }
}
