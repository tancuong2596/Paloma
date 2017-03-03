package cit.edu.paloma;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import cit.edu.paloma.datamodals.User;
import cit.edu.paloma.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {
    public static final int CREATE_NEW_USER_WITH_INFO_RC = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView mAvatarImage;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // todo: for debugging
        //mFirebaseAuth.signOut();

        mAvatarImage = (ImageView) findViewById(R.id.avatar_image);

        findViewById(R.id.user_name_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        setupAuthStateListener();

        // todo: for debugging
    }

    private void setupAuthStateListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                } else {
                    mCurrentUser = user;
                    FirebaseUtils
                            .getUsersRef()
                            .push()
                            .setValue(new User(
                                    user.getUid(),
                                    user.getEmail(),
                                    user.getDisplayName(),
                                    user.getPhotoUrl().toString(),
                                    true
                            ));
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
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    Bundle bundle = intent.getExtras();
                    bundle.putString("userId", uid);

                    FirebaseUtils
                            .findUserRefByUserId(uid)
                            .setValue(User.fromBundle(bundle));
                } else {
                    Log.v(TAG, "Current user is null");
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
}
