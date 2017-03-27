package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
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
    private static final int THUMBNAILS_IMAGE_WIDTH = 256;
    private final Context mContext;
    private final ArrayList<Message> mMessagesList;
    private final String mGroupId;
    private ChildEventListener mChildEventListener;

    public MessagesListAdapter(@NonNull Context context, String groupId) {
        this.mContext = context;
        this.mMessagesList = new ArrayList<>();
        this.mGroupId = groupId;
        setupMessagesChildEventListener();
    }

    private void setupMessagesChildEventListener() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message message = dataSnapshot.getValue(Message.class);

                if (prevChildKey == null) {
                    mMessagesList.add(0, message);
                } else {
                    int prevChildIndex = indexOf(prevChildKey);
                    mMessagesList.add(prevChildIndex + 1, message);
                }
                notifyDataSetChanged();
            }

            private int indexOf(String prevChildKey) {
                for (int i = 0; i < mMessagesList.size(); i++) {
                    if (mMessagesList.get(i).getMessageId().equals(prevChildKey)) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Message message = dataSnapshot.getValue(Message.class);

                int childIndex = indexOf(message.getMessageId());
                mMessagesList.set(childIndex, message);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);

                int childIndex = indexOf(message.getMessageId());
                mMessagesList.remove(childIndex);

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
                .orderByChild("timestamp/date")
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
        textItemMessageContentText.setText((String) message.getContent().get("content"));

        return convertView;
    }

    private View getImageMessageView(Message message, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || convertView.findViewById(R.id.image_item_avatar_image) == null) {
            convertView = layoutInflater.inflate(R.layout.image_message_list_view_item, parent, false);
        }

        final ImageView imageItemAvatarImage = (ImageView) convertView.findViewById(R.id.image_item_avatar_image);
        final TextView imageItemFullNameText = (TextView) convertView.findViewById(R.id.image_item_full_name_text);
        final TextView imageItemSendTimeText = (TextView) convertView.findViewById(R.id.image_item_send_time_text);
        final ImageView imageItemMessageContentImage = (ImageView) convertView.findViewById(R.id.image_message_content_image);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.image_message_progress_bar);

        setUserInfo(message, imageItemAvatarImage, imageItemFullNameText);
        imageItemSendTimeText.setText(DateTimeUtils.getReadableDateTime((Long) message.getTimestamp().get("date")));

        HashMap<String, Object> imageContent = message.getContent();
        int imageWidth = Integer.parseInt(imageContent.get("width").toString());
        int imageHeight = Integer.parseInt(imageContent.get("height").toString());
        double ratio = (double) imageHeight / imageWidth;

        imageItemMessageContentImage.getLayoutParams().height = (int) (THUMBNAILS_IMAGE_WIDTH * ratio);
        imageItemMessageContentImage.getLayoutParams().width = THUMBNAILS_IMAGE_WIDTH;

        imageItemMessageContentImage.requestLayout();

        progressBar.setVisibility(View.VISIBLE);
        Picasso
                .with(mContext)
                .load((String) imageContent.get("content"))
                .into(imageItemMessageContentImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.INVISIBLE);
                        imageItemMessageContentImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_failed));
                    }
                });

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
                .orderByChild("timestamp/date")
                .removeEventListener(mChildEventListener);
    }
}
