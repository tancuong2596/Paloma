package cit.edu.paloma.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import cit.edu.paloma.datamodals.ChatGroup;
import cit.edu.paloma.datamodals.User;

public class FirebaseUtils {
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    public static DatabaseReference getRootRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getUsersRef() {
        return FirebaseDatabase.getInstance().getReference().child("users");
    }

    public static DatabaseReference getChatGroupsRef() {
        return FirebaseDatabase.getInstance().getReference().child("chatGroups");
    }

    public static DatabaseReference getChatGroupsOfMemberRef() {
        return FirebaseDatabase.getInstance().getReference().child("chatGroupsOfMember");
    }

    public static void updateUsersChildren(DatabaseReference userRef,
                                           User newUserInfo,
                                           @Nullable DatabaseReference.CompletionListener completionListener) {

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(userRef.getKey(), newUserInfo.topMap());

        FirebaseUtils
                .getUsersRef()
                .updateChildren(updateChildren, completionListener);
    }

    public static DatabaseReference createNewChatGroup(final ArrayList<Object[]> members, @Nullable OnCompleteListener onCompleteListener) {
        DatabaseReference chatGroupRef = FirebaseUtils
                .getChatGroupsRef()
                .push();

        String groupChatId = chatGroupRef.getKey();
        StringBuilder groupNameBuilder = new StringBuilder();
        ArrayList<String> groupMembersUid = new ArrayList<>();

        for (Object[] member : members) {
            User user = (User) member[1];
            groupNameBuilder.append(user.getFullName()).append(", ");
            groupMembersUid.add(user.getUserId());
            FirebaseUtils
                    .getChatGroupsOfMemberRef()
                    .child(user.getUserId())
                    .push()
                    .setValue(groupChatId);
        }

        ChatGroup chatGroup = new ChatGroup(
                groupChatId,
                groupNameBuilder.substring(0, groupNameBuilder.length() - 2),
                groupMembersUid,
                Collections.emptyList(),
                System.currentTimeMillis()
        );

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(groupChatId, chatGroup.toMap());

        FirebaseUtils
                .getChatGroupsRef()
                .updateChildren(updateChildren);

        return chatGroupRef;
    }
}
