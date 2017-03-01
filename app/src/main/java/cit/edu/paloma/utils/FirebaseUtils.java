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

    public static User findUserWithUserId(final String uid) {
        final User user = new User();

        FirebaseUtils
                .getUsersRef()
                .orderByChild("userId")
                .equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            Log.v(TAG, "User with UID " + uid + " does not exist");
                        } else if (dataSnapshot.getChildrenCount() > 1) {
                            Log.v(TAG, "User with UID " + uid + " has multiple entries");
                        } else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getValue(User.class);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return user;
    }

}
