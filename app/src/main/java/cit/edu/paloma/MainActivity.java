package cit.edu.paloma;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cit.edu.paloma.utils.SignInUtils;

public class MainActivity extends AppCompatActivity {

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

        ((TextView) findViewById(R.id.text_message)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInUtils.signOut(new ResultCallback() {
                    @Override
                    public void onResult(@NonNull Result result) {
                        Log.v(TAG, "result of sign-out");
                    }
                });
            }
        });

        setupAuthStateListener();
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
                    Log.v(TAG, String.format("%s: %s: %s",
                            user.getDisplayName(),
                            user.getEmail(),
                            user.getPhotoUrl()
                    ));
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
}
