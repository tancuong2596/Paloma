package cit.edu.paloma.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.HashSet;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.ChatGroup;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

public class AddFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PARAM_ADDED_USERS_ID = "PARAM_ADDED_USERS_ID";
    public static final String PARAM_GROUP_CHAT_ID = "PARAM_GROUP_CHAT_ID";
    private static final String TAG = AddFriendsActivity.class.getSimpleName();

    private ActionBar mActionBar;
    private ListView mListView;
    private HashSet<String> mAddedUserId;

    private Button mSearchButton;
    private EditText mSearchBox;
    private ArrayAdapter<User> mAdapter;
    private TextView mSearchingText;
    private TextView mNoResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initViews();
    }

    private void initViews() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle("Add friends to group");
        }

        mAddedUserId = new HashSet<>(getIntent().getStringArrayListExtra(PARAM_ADDED_USERS_ID));

        mSearchBox = (EditText) findViewById(R.id.add_friends_search_box);

        mListView = (ListView) findViewById(R.id.add_friends_group_member_list_view);
        mListView.setAdapter(new ArrayAdapter<User>(this, 0) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.user_list_view_item, parent, false);
                }

                final User user = getItem(position);

                final ImageView usrAvatarImage = (ImageView) convertView.findViewById(R.id.usr_avatar_image);
                final TextView usrMainLeftInfoText = (TextView) convertView.findViewById(R.id.usr_main_left_info_text);
                final TextView usrSubLeftInfoText = (TextView) convertView.findViewById(R.id.usr_sub_left_info_text);
                final View usrLeftIndicatorView = convertView.findViewById(R.id.usr_left_indicator_view);
                final Button usrRightButton = (Button) convertView.findViewById(R.id.usr_right_button);
                TextView usrRightInfoText = (TextView) convertView.findViewById(R.id.usr_right_info_text);

                usrRightInfoText.setVisibility(View.GONE);

                FirebaseUtils
                        .getUsersRef()
                        .child(user.getUserId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User usr = dataSnapshot.getValue(User.class);
                                Picasso
                                        .with(AddFriendsActivity.this)
                                        .load(usr.getAvatar())
                                        .into(usrAvatarImage);
                                usrMainLeftInfoText.setText(usr.getFullName());
                                usrSubLeftInfoText.setText(usr.getEmail());
                                if (usr.isOnline()) {
                                    usrLeftIndicatorView.setBackground(ContextCompat.getDrawable(AddFriendsActivity.this, R.drawable.is_online));
                                } else {
                                    usrLeftIndicatorView.setBackground(ContextCompat.getDrawable(AddFriendsActivity.this, R.drawable.is_offline));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                if (mAddedUserId.contains(user.getUserId())) {
                    usrRightButton.setText("remove");
                } else {
                    usrRightButton.setText("add");
                }

                usrRightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (usrRightButton.getText().toString().equalsIgnoreCase("add")) {
                            mAddedUserId.add(user.getUserId());
                            usrRightButton.setText("remove");
                        } else if (mAddedUserId.size() > 2) {
                            mAddedUserId.remove(user.getUserId());
                            usrRightButton.setText("add");
                        }
                    }
                });

                return convertView;
            }
        });

        mAdapter = (ArrayAdapter<User>) mListView.getAdapter();

        mSearchButton = (Button) findViewById(R.id.add_friends_search_button);
        mSearchButton.setOnClickListener(this);

        mSearchingText = (TextView) findViewById(R.id.add_friends_searching_text);

        mNoResultText = (TextView) findViewById(R.id.add_friends_no_result_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);

        switch (item.getItemId()) {
            case R.id.action_apply:
                FirebaseUtils
                        .getChatGroupsRef()
                        .child(groupId)
                        .runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                ChatGroup chatGroup = mutableData.getValue(ChatGroup.class);

                                if (chatGroup == null) {
                                    return Transaction.success(mutableData);
                                }

                                HashMap<String, Object> members = new HashMap<>();
                                for (String id : mAddedUserId) {
                                    members.put(id, "");
                                }
                                chatGroup.setMembers(members);
                                mutableData.setValue(chatGroup);

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar(boolean b) {
        if (b) {
            mSearchingText.setVisibility(View.VISIBLE);
            mNoResultText.setVisibility(View.GONE);
        } else {
            mSearchingText.setVisibility(View.GONE);
            if (mAdapter.isEmpty()) {
                mNoResultText.setVisibility(View.VISIBLE);
            } else {
                mNoResultText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_friends_search_button:
                if (mSearchingText.getVisibility() == View.VISIBLE) {
                    return;
                }

                showProgressBar(true);
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();

                final String pattern = mSearchBox.getText().toString();

                final FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseUtils
                        .getUsersRef()
                        .orderByChild("email")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    User user = snapshot.getValue(User.class);
                                    String email = user.getEmail().toLowerCase();
                                    if (email.contains(pattern)) {
                                        if (!email.equalsIgnoreCase(firebaseCurrentUser.getEmail())) {
                                            mAdapter.add(user);
                                        }
                                    }
                                }

                                mAdapter.notifyDataSetChanged();
                                showProgressBar(false);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                mAdapter.clear();
                                mAdapter.notifyDataSetChanged();
                                showProgressBar(false);
                            }
                        });

                break;
        }
    }
}
