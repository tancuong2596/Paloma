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

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cit.edu.paloma.MainActivity;
import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.ChatGroup;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

/**
 * Created by charlie on 3/5/17.
 */
public class FriendListAdapter extends BaseAdapter {

    public interface AcceptFriendInvitation {
        void onAcceptFriendInvitation(User invitingFriend);
    }

    private static final String TAG = FriendListAdapter.class.getSimpleName();
    private ListView mListView;
    private Context mContext;
    private ArrayList<ChatGroup> mData;


    public FriendListAdapter(Context context, ListView listView) {
        this.mContext = context;
        this.mData = new ArrayList<>();
        this.mListView = listView;
    }


    private View updateFriend(View item, final User friend) {
        ImageView avatarImage = (ImageView) item.findViewById(R.id.usr_avatar_image);
        Picasso
                .with(mContext)
                .load(friend.getAvatar())
                .into(avatarImage);

        TextView friendNameText = (TextView) item.findViewById(R.id.usr_main_left_info_text);
        friendNameText.setText(friend.getFullName());

        View onlineIndicatorView = item.findViewById(R.id.usr_left_indicator_view);
        onlineIndicatorView.setBackground(friend.isOnline() ?
                ContextCompat.getDrawable(mContext, R.drawable.is_online) :
                ContextCompat.getDrawable(mContext, R.drawable.is_offline)
        );

        TextView emailText = (TextView) item.findViewById(R.id.usr_sub_left_info_text);
        emailText.setText(friend.getEmail());

        Button addFriendButton = (Button) item.findViewById(R.id.usr_right_button);
        addFriendButton.setVisibility(View.GONE);

        return item;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ChatGroup getItem(int i) {
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

        ChatGroup chatGroup = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_list_view_item, parent, false);
        }


    }
}
