package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.User;

public class SuggestedFriendsListAdapter extends ArrayAdapter<Object[]> {
    private static final String TAG = SuggestedFriendsListAdapter.class.getSimpleName();
    private static final String MEMBER_REMOVE = "remove";
    private static final String MEMBER_ADD = "add";
    private HashMap<String, Integer> mMemberIndexMap;

    public SuggestedFriendsListAdapter(Context context) {
        super(context, 0, new ArrayList<Object[]>());
    }

    public interface AddFriendListener {
        void onAddFriend(int index, Object[] params);
    }

    public void initializeMemberSet() {
        mMemberIndexMap = new HashMap<>();
    }

    public ArrayList<Object[]> getMembers() {
        ArrayList<Object[]> members = new ArrayList<>();
        for (String uid : mMemberIndexMap.keySet()) {
            int index = mMemberIndexMap.get(uid);
            members.add(getItem(index));
        }
        return members;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_list_view_item, parent, false);
        }

        final Object[] params = getItem(position);

        if (params == null) {
            Log.v(TAG, "Item at " + position + " is null");
            return convertView;
        }

        final User friend = (User) params[1];

        ImageView avatarImage = (ImageView) convertView.findViewById(R.id.usr_avatar_image);
        TextView friendNameText = (TextView) convertView.findViewById(R.id.usr_main_left_info_text);
        View onlineIndicatorView = convertView.findViewById(R.id.usr_left_indicator_view);
        TextView emailText = (TextView) convertView.findViewById(R.id.usr_sub_left_info_text);
        final Button addFriendButton = (Button) convertView.findViewById(R.id.usr_right_button);

        convertView.findViewById(R.id.usr_right_info_text).setVisibility(View.GONE);

        try {
            Picasso
                    .with(getContext())
                    .load(friend.getAvatar())
                    .into(avatarImage);
        } catch (NullPointerException e) {
            avatarImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.user_placeholder));
        }

        onlineIndicatorView.setBackground(friend.isOnline() ?
                ContextCompat.getDrawable(getContext(), R.drawable.is_online) :
                ContextCompat.getDrawable(getContext(), R.drawable.is_offline)
        );

        friendNameText.setText(friend.getFullName());

        emailText.setText(friend.getEmail());

        if (mMemberIndexMap == null) {
            initializeMemberSet();
        }

        if (mMemberIndexMap.containsKey(friend.getUserId())) {
            addFriendButton.setText(MEMBER_REMOVE);
        } else {
            addFriendButton.setText(MEMBER_ADD);
        }

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addFriendButton.getText().toString().equalsIgnoreCase(MEMBER_ADD)) {
                    addFriendButton.setText(MEMBER_REMOVE);
                    mMemberIndexMap.put(friend.getUserId(), position);
                } else {
                    addFriendButton.setText(MEMBER_ADD);
                    mMemberIndexMap.remove(friend.getUserId());
                }
            }
        });

        return convertView;
    }
}
