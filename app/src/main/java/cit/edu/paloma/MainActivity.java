package cit.edu.paloma;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cit.edu.paloma.adapters.FriendListAdapter;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CREATE_NEW_USER_WITH_INFO_RC = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private ListView mFriendList;
    private DatabaseReference mCurrentUserRef;
    private FriendListAdapter mFriendListAdapter;
    private ValueEventListener mCurrentUserValueChanged;
    private Toolbar mToolbar;
    private ImageView mAvatarImageAction;
    private TextView mUserFullnameTextAction;
    private TextView mEmailTextAction;
    private ImageView mSearchBoxImageAction;
    private EditText mSearchBoxEditAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFriendListAdapter = new FriendListAdapter(this);

        mFriendList = (ListView) findViewById(R.id.friends_list);
        mFriendList.setAdapter(mFriendListAdapter);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mAvatarImageAction = (ImageView) findViewById(R.id.ac_avatar_image);

        mUserFullnameTextAction = (TextView) findViewById(R.id.ac_user_full_name_text);

        mEmailTextAction = (TextView) findViewById(R.id.ac_email_text);

        mSearchBoxImageAction = (ImageView) findViewById(R.id.ac_search_image);
        mSearchBoxImageAction.setOnClickListener(this);

        mSearchBoxEditAction = (EditText) findViewById(R.id.ac_search_box);

        setupAuthStateListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_logout:
                FirebaseAuth.getInstance().signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleSearchBox() {
        // TODO: implement this
    }

    private void showUserInfo() {
        mUserFullnameTextAction.setText(mCurrentUser.getDisplayName());
        mEmailTextAction.setText(mCurrentUser.getEmail());
        Picasso
                .with(this)
                .load(mCurrentUser.getPhotoUrl())
                .into(mAvatarImageAction);
    }

    private void setupAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                } else {
                    mCurrentUser = user;

                    showUserInfo();

                    mCurrentUserValueChanged = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 1) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().child("online").setValue(Boolean.TRUE);
                                }
                            } else {
                                FirebaseUtils
                                        .getUsersRef()
                                        .push()
                                        .setValue(new User(
                                                user.getUid(),
                                                user.getEmail(),
                                                user.getDisplayName(),
                                                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "",
                                                "",
                                                true
                                        ));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    FirebaseUtils
                            .getUsersRef()
                            .orderByChild("userId")
                            .equalTo(user.getUid())
                            .addValueEventListener(mCurrentUserValueChanged);
                }
            }
        };

        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        switch (requestCode) {
            case CREATE_NEW_USER_WITH_INFO_RC:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ac_search_image:
                toggleSearchBox();
                break;
        }
    }
}
