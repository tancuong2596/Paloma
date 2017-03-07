package cit.edu.paloma;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, SignInFragment.UserSignInSuccessful {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mCurrentUserRef;
    private ValueEventListener mCurrentUserValueChanged;
    private Toolbar mToolbar;
    private ImageView mAvatarImageAction;
    private TextView mUserFullnameTextAction;
    private TextView mEmailTextAction;
    private ImageView mSearchBoxImageAction;
    private EditText mFriendEmailEditAction;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    private FragmentManager mFragmentManager;
    private ImageView mBackImageAction;

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
    }

    public GoogleSignInOptions getGoogleSignInOptions() {
        return mGoogleSignInOptions;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void navigateTo(int fragmentId) throws Resources.NotFoundException {
        switch (fragmentId) {
            case R.layout.fragment_chat:
                mFriendEmailEditAction.setVisibility(View.GONE);
                mBackImageAction.setVisibility(View.GONE);

                mAvatarImageAction.setVisibility(View.VISIBLE);
                mUserFullnameTextAction.setVisibility(View.VISIBLE);
                mEmailTextAction.setVisibility(View.VISIBLE);
                mSearchBoxImageAction.setVisibility(View.VISIBLE);

                mToolbar.setVisibility(View.VISIBLE);

                mFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChatFragment())
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
                mFriendEmailEditAction.setVisibility(View.VISIBLE);
                mBackImageAction.setVisibility(View.VISIBLE);

                mAvatarImageAction.setVisibility(View.GONE);
                mUserFullnameTextAction.setVisibility(View.GONE);
                mEmailTextAction.setVisibility(View.GONE);
                mSearchBoxImageAction.setVisibility(View.GONE);

                mFragmentManager
                        .beginTransaction()
                        .add(R.id.fragment_container, new FindFriendsFragment())
                        .commit();
                break;
            default:
                throw new Resources.NotFoundException("Fragment with id " + fragmentId + " does not exist");
        }
    }

    private void initViews() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mAvatarImageAction = (ImageView) findViewById(R.id.ac_avatar_image);

        mUserFullnameTextAction = (TextView) findViewById(R.id.ac_user_full_name_text);

        mEmailTextAction = (TextView) findViewById(R.id.ac_email_text);

        mSearchBoxImageAction = (ImageView) findViewById(R.id.ac_search_image);
        mSearchBoxImageAction.setOnClickListener(this);

        mBackImageAction = (ImageView) findViewById(R.id.ac_back_image);
        mBackImageAction.setOnClickListener(this);

        mFriendEmailEditAction = (EditText) findViewById(R.id.ac_friend_email_edit);

        mFragmentManager = getSupportFragmentManager();
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
                    navigateTo(R.layout.fragment_sign_in);
                } else {
                    Log.v(TAG, user.toString());
                    mCurrentUser = user;

                    navigateTo(R.layout.fragment_chat);
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
                                                true,
                                                Collections.emptyList()
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
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ac_search_image:
                navigateTo(R.layout.fragment_find_friends);
                break;
            case R.id.ac_back_image:
                mFragmentManager.popBackStack();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onUserSignInSuccessful() {
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
        mCurrentUser = null;
    }
}
