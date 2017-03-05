package cit.edu.paloma.utils;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cit.edu.paloma.datamodals.User;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    public static DatabaseReference getRootRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getUsersRef() {
        return FirebaseDatabase.getInstance().getReference().child("users");
    }

    public static User findUserByUserId(final String uid) {
        final User[] user = {new User()};

        FirebaseUtils
                .getUsersRef()
                .orderByChild("userId")
                .equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            user[0] = null;
                        } else if (dataSnapshot.getChildrenCount() > 1) {
                            Log.v(TAG, "User with UID " + uid + " has multiple instances");
                        } else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                user[0] = snapshot.getValue(User.class);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        return user[0];
    }

    public static DatabaseReference findUserRefByUserId(final String uid) {
        final DatabaseReference[] userRef = {null};

        FirebaseUtils
                .getUsersRef()
                .orderByChild("userId")
                .equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            userRef[0] = null;
                        } else if (dataSnapshot.getChildrenCount() > 1) {
                            Log.v(TAG, "In callback: User with UID " + uid + " has multiple instances " + dataSnapshot.getChildrenCount());
                        } else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                userRef[0] = snapshot.getRef();
                                Log.v(TAG, userRef[0] == null ? "null" : userRef[0].toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Log.v(TAG, "out of callback: " + (userRef[0] == null ? "null" : userRef[0].toString()));
        return userRef[0];


    }

}
