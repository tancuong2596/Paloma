package cit.edu.paloma.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cit.edu.paloma.MainActivity;
import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.User;

/**
 * Created by charlie on 3/5/17.
 */
public class FriendListAdapter extends ArrayAdapter<User> {
    public FriendListAdapter(Context context) {
        super(context, 0, new ArrayList<User>());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_item, parent);
        }

        User friend = getItem(position);

        ImageView avatarText = (ImageView) convertView.findViewById(R.id.fi_avatar_image);
        TextView friendNameText = (TextView) convertView.findViewById(R.id.fi_friend_name_text);
        TextView recentMessageText = (TextView) convertView.findViewById(R.id.fi_recent_msg_text);
        TextView emailText = (TextView) convertView.findViewById(R.id.fi_friend_email_text);

        avatarText.setImageURI(Uri.parse(friend.getAvatar() == null ? "" : friend.getAvatar()));
        friendNameText.setText(friend.getFullName());
        recentMessageText.setText(friend.getRecentMessage());
        emailText.setText(friend.getEmail());

        return convertView;
    }
}
