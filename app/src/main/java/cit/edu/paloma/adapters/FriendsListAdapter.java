package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.ChatGroup;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

/**
 * Created by charlie on 3/5/17.
 */
public class FriendsListAdapter extends BaseAdapter {
    private static final String TAG = FriendsListAdapter.class.getSimpleName();
    private ListView mListView;
    private Context mContext;
    private ArrayList<ChatGroup> mChatGroups;

    public FriendsListAdapter(Context context, ListView listView) {
        mContext = context;
        mListView = listView;

        mChatGroups = new ArrayList<>();

        setupChatGroupsChildEventListener();
    }

    private void setupChatGroupsChildEventListener() {
        final FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseCurrentUser != null) {
            FirebaseUtils
                    .getChatGroupsRef()
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {
                            ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);

                            if (!chatGroup.getMembers().containsKey(firebaseCurrentUser.getUid())) {
                                return;
                            }

                            if (prevChildName == null) {
                                mChatGroups.add(0, chatGroup);
                            } else {
                                Integer prevChildIndex = indexOf(prevChildName);
                                mChatGroups.add(prevChildIndex + 1, chatGroup);
                            }

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildName) {
                            ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);

                            if (!chatGroup.getMembers().containsKey(firebaseCurrentUser.getUid())) {
                                return;
                            }

                            String childName = dataSnapshot.getKey();

                            Integer childIndex = indexOf(childName);
                            mChatGroups.set(childIndex, chatGroup);

                            notifyDataSetChanged();
                        }

                        private Integer indexOf(String childName) {
                            for (int i = 0; i < mChatGroups.size(); i++) {
                                if (mChatGroups.get(i).getGroupId().equals(childName)) {
                                    return i;
                                }
                            }
                            return -1;
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);
                            if (!chatGroup.getMembers().containsKey(firebaseCurrentUser.getUid())) {
                                return;
                            }

                            String childName = dataSnapshot.getKey();

                            int childIndex = indexOf(childName);
                            mChatGroups.remove(childIndex);

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    }

    @Override
    public int getCount() {
        return mChatGroups.size();
    }

    @Override
    public ChatGroup getItem(int i) {
        return mChatGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatGroup chatGroup = getItem(position);

        if (chatGroup.getMembers().size() <= 2) {
            return getItemOfCoupleMembers(chatGroup, convertView, parent);
        } else {
            return getItemOfMultipleMembers(chatGroup, convertView, parent);
        }
    }

    private View getItemOfMultipleMembers(ChatGroup chatGroup, View view, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null || view.findViewById(R.id.user1_avatar_image) == null) {
            view = layoutInflater.inflate(R.layout.chat_group_more_list_view_item, parent, false);
        }

        // variables for views
        ImageView user1AvatarImage = (ImageView) view.findViewById(R.id.user1_avatar_image);
        ImageView user2AvatarImage = (ImageView) view.findViewById(R.id.user2_avatar_image);
        ImageView user3AvatarImage = (ImageView) view.findViewById(R.id.user3_avatar_image);
        TextView tripleMainText = (TextView) view.findViewById(R.id.triple_main_text);
        TextView tripleSubText = (TextView) view.findViewById(R.id.triple_sub_text);

        // three first users
        String[] userAvatars = new String[]{"", "", ""};
        int index = 0;
        for (String key : chatGroup.getMembers().keySet()) {
            if (index >= 3) {
                break;
            }
            userAvatars[index] = (String) chatGroup.getMembers().get(key);
            index++;
        }

        // load avatar for three first users
        Picasso
                .with(mContext)
                .load(userAvatars[0])
                .into(user1AvatarImage);
        Picasso
                .with(mContext)
                .load(userAvatars[1])
                .into(user2AvatarImage);
        Picasso
                .with(mContext)
                .load(userAvatars[2])
                .into(user3AvatarImage);

        // set group name which is combination of name of all users
        if (chatGroup.getGroupName() == null || chatGroup.getGroupName().trim().isEmpty()) {
            makeGroupName(chatGroup.getMembers(), tripleMainText);
        } else {
            tripleMainText.setText(chatGroup.getGroupName());
        }

        // set recent message text
        String recentMessage = null;

        if (chatGroup.getRecentMessage() != null && !chatGroup.getRecentMessage().isEmpty()) {
            recentMessage = (chatGroup.getRecentMessage());
        }

        if (recentMessage != null) {
            tripleSubText.setText(recentMessage);
        } else {
            tripleSubText.setText(null);
        }

        return view;
    }

    private void makeGroupName(final HashMap<String, Object> members, final TextView textView) {
        FirebaseUtils
                .getUsersRef()
                .orderByChild("userId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StringBuilder name = new StringBuilder();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (members.containsKey(user.getUserId())) {
                                name.append(user.getFullName()).append(", ");
                            }
                        }

                        int nOthers = members.size() - 3;

                        name = new StringBuilder(name.substring(0, name.length() - 2));

                        if (nOthers >= 1) {
                            name.append(" and ").append(nOthers).append(" other").append(nOthers > 1 ? "s": "");
                        }

                        textView.setText(name.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private View getItemOfCoupleMembers(final ChatGroup chatGroup, View view, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null || view.findViewById(R.id.usr_right_button) == null) {
            view = layoutInflater.inflate(R.layout.user_list_view_item, parent, false);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // variables for views
        ImageView avatarImage = (ImageView) view.findViewById(R.id.usr_avatar_image);
        final TextView mainLeftInfoText = (TextView) view.findViewById(R.id.usr_main_left_info_text);
        final TextView subLeftInfoText = (TextView) view.findViewById(R.id.usr_sub_left_info_text);
        final View leftIndicatorView = view.findViewById(R.id.usr_left_indicator_view);
        Button rightButton = (Button) view.findViewById(R.id.usr_right_button);
        TextView rightInfoText = (TextView) view.findViewById(R.id.usr_right_info_text);

        // the only user different from the current one
        String avatar = "";
        String userId = "";

        if (currentUser == null) {
            return view;
        }

        for (String key : chatGroup.getMembers().keySet()) {
            if (!key.equals(currentUser.getUid())) {
                avatar = (String) chatGroup.getMembers().get(key); 
                userId = key;
                break;
            }
        }

        Picasso
                .with(mContext)
                .load(avatar)
                .into(avatarImage);

        String recentMessage = null;

        if (chatGroup.getRecentMessage() != null && !chatGroup.getRecentMessage().isEmpty()) {
            recentMessage = chatGroup.getRecentMessage();
        }

        if (recentMessage != null) {
            subLeftInfoText.setText(recentMessage);
        } else {
            subLeftInfoText.setText(null);
        }

        FirebaseUtils
                .getUsersRef()
                .orderByChild("userId")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);

                            mainLeftInfoText.setText(user.getFullName());

                            if (subLeftInfoText.getText() == null || subLeftInfoText.getText().toString().isEmpty()) {
                                subLeftInfoText.setText(user.getEmail());
                            }

                            if (user.isOnline()) {
                                leftIndicatorView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.is_online));
                            } else {
                                leftIndicatorView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.is_offline));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        rightButton.setVisibility(View.GONE);
        rightInfoText.setVisibility(View.GONE);

        return view;
    }
}
