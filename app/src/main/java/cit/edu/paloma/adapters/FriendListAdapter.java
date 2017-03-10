package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.User;

/**
 * Created by charlie on 3/5/17.
 */
public class FriendListAdapter extends BaseAdapter implements View.OnClickListener {

    private class FriendListAdapterItem {
        public static final int FRIEND = 0;
        public static final int INVITATION = 1;

        User user;
        int itemType;

        public FriendListAdapterItem() {
        }

        public FriendListAdapterItem(User user, int itemType) {
            this.user = user;
            this.itemType = itemType;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }
    }

    private static final String TAG = FriendListAdapter.class.getSimpleName();
    private ListView mListView;
    private Context mContext;
    private ArrayList<FriendListAdapterItem> mData;
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
                mData.add(new FriendListAdapterItem(newUser, FriendListAdapterItem.FRIEND));
                mDataMap.put(newUser.getUserId(), mData.size() - 1);
            } else {
                if (mListView.getFirstVisiblePosition() <= oldUserIndex &&
                        oldUserIndex <= mListView.getLastVisiblePosition()) {
                    View item = mListView.getChildAt(oldUserIndex - mListView.getFirstVisiblePosition());
                    updateFriend(item, newUser);
                }
                mData.set(oldUserIndex, new FriendListAdapterItem(newUser, FriendListAdapterItem.FRIEND));
            }
        }
        notifyDataSetChanged();
    }

    private View updateFriend(View item, User friend) {
        ImageView avatarImage = (ImageView) item.findViewById(R.id.fi_avatar_image);
        TextView friendNameText = (TextView) item.findViewById(R.id.fi_friend_name_text);
        TextView recentMessageText = (TextView) item.findViewById(R.id.fi_recent_msg_text);

        Picasso
                .with(mContext)
                .load(friend.getAvatar())
                .into(avatarImage);

        friendNameText.setText(friend.getFullName());
        recentMessageText.setText(friend.getRecentMessage());

        return item;
    }

    private View updateInvite(View item, User friend) {
        ImageView avatarImage = (ImageView) item.findViewById(R.id.fii_avatar_image);
        TextView friendNameText = (TextView) item.findViewById(R.id.fii_friend_name_text);
        TextView emailText = (TextView) item.findViewById(R.id.fii_email_text);
        Button acceptButton = (Button) item.findViewById(R.id.fii_accept_button);

        Picasso
                .with(mContext)
                .load(friend.getAvatar())
                .into(avatarImage);

        friendNameText.setText(friend.getFullName());
        emailText.setText(friend.getEmail());
        acceptButton.setOnClickListener(this);

        return item;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fii_accept_button:

                break;
        }
    }

    public void updateInvites(ArrayList<User> updatedInvites) {
        for (User newUser : updatedInvites) {
            Integer oldUserIndex = mDataMap.get(newUser.getUserId());
            if (oldUserIndex == null) {
                mData.add(new FriendListAdapterItem(newUser, FriendListAdapterItem.INVITATION));
                mDataMap.put(newUser.getUserId(), mData.size() - 1);
            } else {
                if (mListView.getFirstVisiblePosition() <= oldUserIndex &&
                        oldUserIndex <= mListView.getLastVisiblePosition()) {
                    View item = mListView.getChildAt(oldUserIndex - mListView.getFirstVisiblePosition());
                    updateFriend(item, newUser);
                }
                mData.set(oldUserIndex, new FriendListAdapterItem(newUser, FriendListAdapterItem.INVITATION));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public FriendListAdapterItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        FriendListAdapterItem item = getItem(position);
        User friend = item.getUser();

        if (item.getItemType() == FriendListAdapterItem.FRIEND) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.friends_list_item_user, parent, false);
            }
            convertView = updateFriend(convertView, friend);
        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.friends_list_item_invitation, parent, false);
            }
            convertView = updateInvite(convertView, friend);
        }
        return convertView;
    }


}
