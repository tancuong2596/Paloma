package cit.edu.paloma.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.User;

public class SuggestedFriendListAdapter extends ArrayAdapter<User> {
    public SuggestedFriendListAdapter(Context context) {
        super(context, 0, new ArrayList<User>());
    }

    public interface AddFriendListener {
        public void onAddFriend(int index, User user);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_item, parent);
        }

        final User friend = getItem(position);

        ImageView avatarText = (ImageView) convertView.findViewById(R.id.sfi_avatar_image);
        TextView friendNameText = (TextView) convertView.findViewById(R.id.sfi_friend_name_text);
        View onlineIndicatorView = convertView.findViewById(R.id.sfi_online_indicator_view);
        TextView emailText = (TextView) convertView.findViewById(R.id.sfi_friend_email_text);
        Button addFriendButton = (Button) convertView.findViewById(R.id.sfi_add_friend_button);

        try {
            avatarText.setImageURI(Uri.parse(friend.getAvatar()));
        } catch (NullPointerException e) {
            avatarText.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.user_placeholder));
        }

        onlineIndicatorView.setBackground(friend.isOnline() ?
                ContextCompat.getDrawable(getContext(), R.drawable.is_online) :
                ContextCompat.getDrawable(getContext(), R.drawable.is_offline)
        );

        friendNameText.setText(friend.getFullName());

        emailText.setText(friend.getEmail());

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddFriendListener) getContext()).onAddFriend(position, friend);
            }
        });

        return convertView;
    }
}
