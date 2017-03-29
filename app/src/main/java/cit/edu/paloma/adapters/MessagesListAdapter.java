package cit.edu.paloma.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.DateTimeUtils;
import cit.edu.paloma.utils.FirebaseUtils;

public class MessagesListAdapter extends BaseAdapter {
    private static final String TAG = MessagesListAdapter.class.getSimpleName();
    private static final ConcurrentHashMap<String, User> cached = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, File> cachedImages = new ConcurrentHashMap<>();
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

    private void setUserInfo(final Message message,
                             final ImageView avatarImage,
                             final TextView fullNameText,
                             TextView sendTimeText) {

        sendTimeText.setText(DateTimeUtils.getReadableDateTime((Long) message.getTimestamp().get("date")));

        if (cached.containsKey(message.getSenderId())) {
            User user = cached.get(message.getSenderId());

            Picasso
                    .with(mContext)
                    .load(user.getAvatar())
                    .into(avatarImage);

            fullNameText.setText(user.getFullName());
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
                                        .into(avatarImage);

                                fullNameText.setText(user.getFullName());

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

        setUserInfo(message, textItemAvatarImage, textItemFullNameText, textItemSendTimeText);

        textItemMessageContentText.setText((String) message.getContent().get("content"));

        return convertView;
    }

    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

        setUserInfo(message, imageItemAvatarImage, imageItemFullNameText, imageItemSendTimeText);

        HashMap<String, Object> imageContent = message.getContent();
        int imageWidth = Integer.parseInt(imageContent.get("width").toString());
        int imageHeight = Integer.parseInt(imageContent.get("height").toString());
        double ratio = (double) imageHeight / imageWidth;

        int newImageHeight = (int) (THUMBNAILS_IMAGE_WIDTH * ratio);
        int newImageWidth = THUMBNAILS_IMAGE_WIDTH;

        imageItemMessageContentImage.getLayoutParams().height = newImageHeight;
        imageItemMessageContentImage.getLayoutParams().width = newImageWidth;
        imageItemMessageContentImage.requestLayout();

        final String url = imageContent.get("content").toString();

        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = calculateInSampleSize(imageWidth, imageHeight, newImageWidth, newImageHeight);

        if (cachedImages.containsKey(url)) {
            progressBar.setVisibility(View.VISIBLE);
            imageItemMessageContentImage.setVisibility(View.INVISIBLE);
            Picasso
                    .with(mContext)
                    .load(cachedImages.get(url))
                    .into(imageItemMessageContentImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                            imageItemMessageContentImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.INVISIBLE);
                            imageItemMessageContentImage.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            try {
                progressBar.setVisibility(View.VISIBLE);
                imageItemMessageContentImage.setVisibility(View.INVISIBLE);
                final File file = File.createTempFile(UUID.randomUUID().toString(), "jpeg", mContext.getCacheDir());
                FirebaseStorage
                        .getInstance()
                        .getReferenceFromUrl(url)
                        .getFile(file)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                                OutputStream out;
                                try {
                                    out = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                imageItemMessageContentImage.setImageBitmap(bitmap);
                                cachedImages.put(url, file);
                                progressBar.setVisibility(View.INVISIBLE);
                                imageItemMessageContentImage.setVisibility(View.VISIBLE);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return convertView;
    }

    private View getFileMessageView(Message message, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || convertView.findViewById(R.id.file_item_avatar_image) == null) {
            convertView = layoutInflater.inflate(R.layout.file_message_list_view_item, parent, false);
        }

        ImageView fileItemAvatarImage = (ImageView) convertView.findViewById(R.id.file_item_avatar_image);
        TextView fileItemFullNameText = (TextView) convertView.findViewById(R.id.file_item_full_name_text);
        TextView fileItemSendTimeText = (TextView) convertView.findViewById(R.id.file_item_send_time_text);
        LinearLayout fileItemFileLinkLayout = (LinearLayout) convertView.findViewById(R.id.file_item_file_link_layout);
        TextView fileItemLinkText = (TextView) convertView.findViewById(R.id.file_item_link_text);

        setUserInfo(message, fileItemAvatarImage, fileItemFullNameText, fileItemSendTimeText);

        fileItemLinkText.setText(message.getContent().get("filename").toString());

        return convertView;
    }

    public void detachAllEventListeners() {
        FirebaseUtils
                .getMessagesRef()
                .child(mGroupId)
                .orderByChild("timestamp/date")
                .removeEventListener(mChildEventListener);
        for (String key : cachedImages.keySet()) {
            cachedImages.get(key).delete();
        }
    }
}
