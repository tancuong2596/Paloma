package cit.edu.paloma.activities;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.fragments.FindFriendsFragment;
import cit.edu.paloma.fragments.FriendsListFragment;
import cit.edu.paloma.fragments.SignInFragment;
import cit.edu.paloma.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_FIND_FRIENDS = "FRAGMENT_FIND_FRIENDS";
    private static final String FRAGMENT_FRIENDS_LIST = "FRAGMENT_FRIENDS_LIST";

    public static final String FRIEND_ACCEPTED = "accepted";
    public static final String FRIEND_PENDING = "pending";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseCurrentUser;
    private DatabaseReference mFirebaseCurrentUserRef;
    private ValueEventListener mCurrentUserValueChanged;
    private Toolbar mToolbar;
    private ImageView mAvatarImageAction;
    private TextView mUserFullNameTextAction;
    private TextView mEmailTextAction;
    private ImageView mSearchBoxImageAction;
    private EditText mFriendEmailEditAction;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private FragmentManager mFragmentManager;
    private ImageView mBackImageAction;
    private User mCurrentUser;
    private ImageView mApplyImageAction;
    private AlertDialog mGroupChatRenameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        setupAuthStateListener();

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();


        mGroupChatRenameDialog = new AlertDialog
                .Builder(this, R.style.DialogTheme)
                .setView(getLayoutInflater().inflate(R.layout.input_box_dialog, null))
                .setTitle("Name your conversation")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    public GoogleSignInOptions getGoogleSignInOptions() {
        return mGoogleSignInOptions;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    private void showSearchBox(boolean yes) {
        int searchVisibility = yes ? View.VISIBLE : View.GONE;
        int informationVisibility = yes ? View.GONE : View.VISIBLE;

        mFriendEmailEditAction.setVisibility(searchVisibility);
        mBackImageAction.setVisibility(searchVisibility);
        mApplyImageAction.setVisibility(searchVisibility);

        mAvatarImageAction.setVisibility(informationVisibility);
        mUserFullNameTextAction.setVisibility(informationVisibility);
        mEmailTextAction.setVisibility(informationVisibility);

        mToolbar.setVisibility(View.VISIBLE);
    }

    public void navigateTo(int fragmentId) throws Resources.NotFoundException {
        switch (fragmentId) {
            case R.layout.fragment_friends_list:
                showSearchBox(false);

                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, new FriendsListFragment(), FRAGMENT_FRIENDS_LIST)
                        .commit();

                break;
            case R.layout.fragment_sign_in:
                mToolbar.setVisibility(View.GONE);

                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SignInFragment())
                        .commit();
                break;
            case R.layout.fragment_find_friends:
                showSearchBox(true);

                mFragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container, new FindFriendsFragment(), FRAGMENT_FIND_FRIENDS)
                        .addToBackStack(null)
                        .commit();

                break;
            default:
                Log.v(TAG, "Fragment with id " + fragmentId + " does not exist");
        }
    }

    private void initViews() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mAvatarImageAction = (ImageView) findViewById(R.id.ac_avatar_image);

        mUserFullNameTextAction = (TextView) findViewById(R.id.ac_user_full_name_text);

        mEmailTextAction = (TextView) findViewById(R.id.ac_email_text);

        mSearchBoxImageAction = (ImageView) findViewById(R.id.ac_search_image);
        mSearchBoxImageAction.setOnClickListener(this);

        mBackImageAction = (ImageView) findViewById(R.id.ac_back_image);
        mBackImageAction.setOnClickListener(this);

        mApplyImageAction = (ImageView) findViewById(R.id.ac_apply_image);
        mApplyImageAction.setOnClickListener(this);

        mFriendEmailEditAction = (EditText) findViewById(R.id.ac_friend_email_edit);

        mFragmentManager = getSupportFragmentManager();
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_logout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showUserInfo() {
        showSearchBox(false);
        mUserFullNameTextAction.setText(mFirebaseCurrentUser.getDisplayName());
        mEmailTextAction.setText(mFirebaseCurrentUser.getEmail());
        Picasso
                .with(this)
                .load(mFirebaseCurrentUser.getPhotoUrl())
                .into(mAvatarImageAction);
    }

    private void setupAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    navigateTo(R.layout.fragment_sign_in);
                } else {
                    mFirebaseCurrentUser = user;

                    navigateTo(R.layout.fragment_friends_list);
                    showUserInfo();

                    mFirebaseCurrentUserRef = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child("/users/" + mFirebaseCurrentUser.getUid());

                    mCurrentUser = new User(
                            user.getUid(),
                            user.getEmail(),
                            user.getDisplayName(),
                            user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "",
                            true
                    );

                    FirebaseUtils
                            .updateUsersChildren(mFirebaseCurrentUserRef, mCurrentUser, null);
                }
            }
        };

        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ac_search_image: {
                if (mFriendEmailEditAction.getVisibility() == View.VISIBLE) {
                    String pattern = mFriendEmailEditAction.getText().toString().trim();

                    if (pattern.isEmpty()) {
                        return;
                    }

                    FindFriendsFragment fragment =
                            (FindFriendsFragment) mFragmentManager.findFragmentByTag(FRAGMENT_FIND_FRIENDS);

                    if (fragment != null) {
                        fragment.findUsersWithPattern(pattern);
                    }
                } else {
                    navigateTo(R.layout.fragment_find_friends);
                }
                break;
            }
            case R.id.ac_back_image: {
                showSearchBox(false);
                mFragmentManager.popBackStack();
                break;
            }
            case R.id.ac_apply_image: {
                FindFriendsFragment fragment =
                        (FindFriendsFragment) mFragmentManager.findFragmentByTag(FRAGMENT_FIND_FRIENDS);

                ArrayList<Object[]> members = fragment.getSelectedMembers();
                members.add(new Object[]{mFirebaseCurrentUserRef, mCurrentUser});

                DatabaseReference ref = FirebaseUtils.createNewChatGroup(members, null);

                mFragmentManager.popBackStack();
                break;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void signOut() {
        showSearchBox(true);

        if (mFirebaseCurrentUser != null) {
            mFirebaseCurrentUserRef
                    .child("online")
                    .setValue(Boolean.FALSE);
        }

        FirebaseAuth.getInstance().signOut();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
        mFirebaseCurrentUser = null;
    }
}
