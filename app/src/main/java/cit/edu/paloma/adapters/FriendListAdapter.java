package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

import cit.edu.paloma.MainActivity;
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

        @Override
        public String toString() {
            return "FriendListAdapterItem{" +
                    "user=" + user +
                    ", itemType=" + itemType +
                    '}';
        }

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


    public FriendListAdapter(Context context, ListView listView) {
        this.mContext = context;
        this.mData = new ArrayList<>();
        this.mListView = listView;
    }

    public void updateUsers(ArrayList<User> updatedFriends, ArrayList<User> updatedInvites) {
        mData.clear();

        for (User newUser : updatedFriends) {
            mData.add(new FriendListAdapterItem(newUser, FriendListAdapterItem.FRIEND));
        }

        for (User newUser : updatedInvites) {
            mData.add(new FriendListAdapterItem(newUser, FriendListAdapterItem.INVITATION));
        }

        notifyDataSetChanged();
    }


    private View updateFriend(View item, User friend) {
        ImageView avatarImage = (ImageView) item.findViewById(R.id.sfi_avatar_image);
        Picasso
                .with(mContext)
                .load(friend.getAvatar())
                .into(avatarImage);

        TextView friendNameText = (TextView) item.findViewById(R.id.sfi_friend_name_text);
        friendNameText.setText(friend.getFullName());

        View onlineIndicatorView = item.findViewById(R.id.sfi_online_indicator_view);
        onlineIndicatorView.setBackground(friend.isOnline() ?
                ContextCompat.getDrawable(mContext, R.drawable.is_online) :
                ContextCompat.getDrawable(mContext, R.drawable.is_offline)
        );

        TextView emailText = (TextView) item.findViewById(R.id.sfi_friend_email_text);
        emailText.setText(friend.getEmail());

        Button addFriendButton = (Button) item.findViewById(R.id.sfi_add_friend_button);
        addFriendButton.setVisibility(View.GONE);
        addFriendButton.setOnClickListener(this);

        return item;
    }

    private View updateInvite(View item, User friend) {
        ImageView avatarImage = (ImageView) item.findViewById(R.id.sfi_avatar_image);
        Picasso
                .with(mContext)
                .load(friend.getAvatar())
                .into(avatarImage);

        TextView friendNameText = (TextView) item.findViewById(R.id.sfi_friend_name_text);
        friendNameText.setText(friend.getFullName());

        View onlineIndicatorView = item.findViewById(R.id.sfi_online_indicator_view);
        onlineIndicatorView.setBackground(friend.isOnline() ?
                ContextCompat.getDrawable(mContext, R.drawable.is_online) :
                ContextCompat.getDrawable(mContext, R.drawable.is_offline)
        );

        TextView emailText = (TextView) item.findViewById(R.id.sfi_friend_email_text);
        emailText.setText(friend.getEmail());

        Button addFriendButton = (Button) item.findViewById(R.id.sfi_add_friend_button);
        addFriendButton.setText(mContext.getString(R.string.accept));
        addFriendButton.setVisibility(View.VISIBLE);

        return item;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sfi_add_friend_button:

                break;
        }
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

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.suggested_friend_item, parent, false);
        }

        Log.v(TAG, item.toString());

        switch (item.getItemType()) {
            case FriendListAdapterItem.FRIEND:
                return updateFriend(convertView, friend);
            case FriendListAdapterItem.INVITATION:
                return updateInvite(convertView, friend);
            default:
                return convertView;
        }
    }
}
