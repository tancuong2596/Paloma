package cit.edu.paloma.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cit.edu.paloma.datamodals.User;

public class FirebaseUtils {
    public static DatabaseReference getRootRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void addUser(final User user) {
        getRootRef()
                .child("users")
                .orderByChild("email")
                .equalTo(user.getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            DatabaseReference ref = getRootRef()
                                    .child("users")
                                    .push();
                            ref.setValue(user);
                        } else {
                            throw new DatabaseException("User with email " + user.getEmail() + " exists");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
