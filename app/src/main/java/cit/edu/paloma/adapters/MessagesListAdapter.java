package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.DateTimeUtils;
import cit.edu.paloma.utils.FirebaseUtils;

public class MessagesListAdapter extends BaseAdapter {
    private static final String TAG = MessagesListAdapter.class.getSimpleName();
    private static final HashMap<String, User> cached = new HashMap<>();
    private final Context mContext;
    private final ArrayList<Message> mMessagesList;
    private final HashMap<String, Integer> mMessageKeyIndexMap;
    private final String mGroupId;
    private ChildEventListener mChildEventListener;

    public MessagesListAdapter(@NonNull Context context, String groupId) {
        this.mContext = context;
        this.mMessagesList = new ArrayList<>();
        this.mMessageKeyIndexMap = new HashMap<>();
        this.mGroupId = groupId;
        setupMessagesChildEventListener();
    }

    private void setupMessagesChildEventListener() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message message = dataSnapshot.getValue(Message.class);

                Log.v(TAG, message.toString());

                if (prevChildKey == null) {
                    mMessagesList.add(0, message);
                    mMessageKeyIndexMap.put(message.getMessageId(), 0);
                } else {
                    int prevChildIndex = mMessageKeyIndexMap.get(prevChildKey);
                    mMessagesList.add(prevChildIndex + 1, message);
                    mMessageKeyIndexMap.put(message.getMessageId(), prevChildIndex + 1);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.v(TAG, "onChildChanged");
                Message message = dataSnapshot.getValue(Message.class);

                int childIndex = mMessageKeyIndexMap.get(message.getMessageId());
                mMessagesList.set(childIndex, message);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);

                int childIndex = mMessageKeyIndexMap.get(message.getMessageId());
                mMessagesList.remove(childIndex);
                mMessageKeyIndexMap.remove(message.getMessageId());

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseUtils
                .getMessagesRef()
                .child(mGroupId)
                .orderByChild("timestamp")
                .addChildEventListener(mChildEventListener);
    }

    @Override
    public int getCount() {
        return mMessagesList.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        switch (message.getContentType()) {
            case Message.FILE:
                return getFileMessageView(message, convertView, parent);
            case Message.IMAGE:
                return getImageMessageView(message, convertView, parent);
            case Message.TEXT:
                return getTextMessageView(message, convertView, parent);
            default:
                Log.v(TAG, "This is not supported type");
                return convertView;
        }
    }

    private void setUserInfo(final Message message, final ImageView textItemAvatarImage, final TextView textItemFullNameText) {
        if (cached.containsKey(message.getSenderId())) {
            User user = cached.get(message.getSenderId());

            Picasso
                    .with(mContext)
                    .load(user.getAvatar())
                    .into(textItemAvatarImage);

            textItemFullNameText.setText(user.getFullName());
        } else {
            FirebaseUtils
                    .getUsersRef()
                    .orderByChild("userId")
                    .equalTo(message.getSenderId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                Picasso
                                        .with(mContext)
                                        .load(user.getAvatar())
                                        .into(textItemAvatarImage);

                                textItemFullNameText.setText(user.getFullName());

                                cached.put(message.getSenderId(), user);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private View getTextMessageView(final Message message, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || convertView.findViewById(R.id.text_item_avatar_image) == null) {
            convertView = layoutInflater.inflate(R.layout.text_message_list_view_item, parent, false);
        }

        final ImageView textItemAvatarImage = (ImageView) convertView.findViewById(R.id.text_item_avatar_image);
        final TextView textItemFullNameText = (TextView) convertView.findViewById(R.id.text_item_full_name_text);
        final TextView textItemSendTimeText = (TextView) convertView.findViewById(R.id.text_item_send_time_text);
        TextView textItemMessageContentText = (TextView) convertView.findViewById(R.id.text_item_message_content_text);

        textItemSendTimeText.setText(DateTimeUtils.getReadableDateTime((Long) message.getTimestamp().get("date")));
        setUserInfo(message, textItemAvatarImage, textItemFullNameText);
        textItemMessageContentText.setText(message.getContent());

        return convertView;
    }

    private View getImageMessageView(Message message, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || convertView.findViewById(R.id.image_item_avatar_image) == null) {
            convertView = layoutInflater.inflate(R.layout.image_message_list_view_item, parent, false);
        }


        return convertView;
    }

    private View getFileMessageView(Message message, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || convertView.findViewById(R.id.file_item_avatar_image) == null) {
            convertView = layoutInflater.inflate(R.layout.file_message_list_view_item, parent, false);
        }


        return convertView;
    }

    public void detachAllEventListeners() {
        FirebaseUtils
                .getMessagesRef()
                .child(mGroupId)
                .orderByChild("timestamp")
                .removeEventListener(mChildEventListener);
    }
}
