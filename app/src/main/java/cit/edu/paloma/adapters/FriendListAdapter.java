package cit.edu.paloma.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.fragments.FriendsListFragment;

/**
 * Created by charlie on 3/5/17.
 */
public class FriendListAdapter extends BaseAdapter {
    private static final String TAG = FriendListAdapter.class.getSimpleName();
    private ListView mListView;
    private Context mContext;
    private ArrayList<User> mData;
    private HashMap<String, Integer> mDataMap;


    public FriendListAdapter(Context context, ListView listView) {
        this.mContext = context;
        this.mData = new ArrayList<>();
        this.mDataMap = new HashMap<>();
        this.mListView = listView;
    }

    public void updateFriends(ArrayList<User> updatedFriends) {
        for (User newUser : updatedFriends) {
            Integer oldUserIndex = mDataMap.get(newUser.getUserId());
            if (oldUserIndex == null) {
                mData.add(newUser);
                mDataMap.put(newUser.getUserId(), mData.size() - 1);
            } else {
                if (mListView.getFirstVisiblePosition() <= oldUserIndex && oldUserIndex <= mListView.getLastVisiblePosition()) {
                    View item = mListView.getChildAt(oldUserIndex - mListView.getFirstVisiblePosition());
                    updateView(item, newUser);
                }
                mData.set(oldUserIndex, newUser);
            }
        }
        notifyDataSetChanged();
    }

    private void updateView(View item, User newUser) {
        // todo: implement
    }

    public void updateInvites(ArrayList<User> updatedInvites) {
        // todo: implement
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public User getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_item, parent, false);
        }

        User friend = getItem(position);

        Log.v(TAG, "at position = " + position);
        Log.v(TAG, friend.toString());

        ImageView avatarImage = (ImageView) convertView.findViewById(R.id.fi_avatar_image);
        TextView friendNameText = (TextView) convertView.findViewById(R.id.fi_friend_name_text);
        TextView recentMessageText = (TextView) convertView.findViewById(R.id.fi_recent_msg_text);
        TextView emailText = (TextView) convertView.findViewById(R.id.fi_friend_email_text);

        Picasso
                .with(mContext)
                .load(friend.getAvatar())
                .into(avatarImage);

        friendNameText.setText(friend.getFullName());
        recentMessageText.setText(friend.getRecentMessage());
        emailText.setText("(" + friend.getEmail() + ")");

        return convertView;
    }
}
