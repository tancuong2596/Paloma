package cit.edu.paloma;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import cit.edu.paloma.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {
    public static final int CREATE_NEW_USER_WITH_INFO_RC = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

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
                    final String email = firebaseAuth.getCurrentUser().getEmail();
                    final String displayName = firebaseAuth.getCurrentUser().getDisplayName();
                    final String photoUrl = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();




                    if (email == null || email.trim().isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("email", email);
                        bundle.putString("fullName", displayName);
                        bundle.putString("avatar", photoUrl);

                        startActivityForResult(intent, CREATE_NEW_USER_WITH_INFO_RC, bundle);
                    }

                    // todo: find and get user data from database


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
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // todo: push new user to database
                FirebaseUtils.addUser()
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
}
