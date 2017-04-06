package cit.edu.paloma.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.datamodals.ChatGroup;
import cit.edu.paloma.datamodals.Message;
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

    public static DatabaseReference getMessagesRef() {
        return FirebaseDatabase.getInstance().getReference().child("messages");
    }

    public static DatabaseReference sendMessage(Message message,
                                                @Nullable OnCompleteListener onCompleteListener) {
        DatabaseReference newMessageRef = FirebaseUtils
                .getMessagesRef()
                .push();

        String messageKey = newMessageRef.getKey();

        message.setMessageId(messageKey);

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(message.getGroupChatId() + "/" + messageKey, message.toMap());

        FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> messageContent = message.getContent();
        StringBuilder recentMessage = new StringBuilder();

        if (firebaseCurrentUser != null) {
            recentMessage.append(firebaseCurrentUser.getDisplayName());
            if (messageContent.containsKey("filename")) {
                recentMessage.append(": ").append(messageContent.get("filename"));
            } else if (messageContent.containsKey("content")) {
                recentMessage.append(": ").append(messageContent.get("content"));
            }
        }

        FirebaseUtils
                .getChatGroupsRef()
                .child(message.getGroupChatId())
                .child("recentMessage")
                .setValue(recentMessage.toString());

        FirebaseUtils
                .getMessagesRef()
                .updateChildren(updateChildren);

        return newMessageRef;
    }

    public static void updateUsersChildren(DatabaseReference userRef,
                                           User newUserInfo,
                                           @Nullable DatabaseReference.CompletionListener completionListener) {

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(userRef.getKey(), newUserInfo.toMap());

        FirebaseUtils
                .getUsersRef()
                .updateChildren(updateChildren, completionListener);
    }

    public static DatabaseReference createNewChatGroup(final ArrayList<Object[]> members,
                                                       @Nullable OnCompleteListener onCompleteListener) {
        DatabaseReference chatGroupRef = FirebaseUtils
                .getChatGroupsRef()
                .push();

        String groupChatId = chatGroupRef.getKey();
        HashMap<String, Object> groupMembersUid = new HashMap<>();

        for (Object[] member : members) {
            User user = (User) member[1];
            groupMembersUid.put(user.getUserId(), user.getAvatar());
            Log.v(TAG, String.format("%s\n", user.getUserId()));
        }

        ChatGroup chatGroup = new ChatGroup(
                groupChatId,
                "",
                "",
                groupMembersUid
        );

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(groupChatId, chatGroup.toMap());

        FirebaseUtils
                .getChatGroupsRef()
                .updateChildren(updateChildren);

        return chatGroupRef;
    }

    public static void deleteMessage(final Message item) {
        FirebaseUtils
                .getMessagesRef()
                .child(item.getGroupChatId() + "/" + item.getMessageId())
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        HashMap<String, Object> content = item.getContent();
                        switch (item.getContentType()) {
                            case Message.IMAGE:
                            case Message.FILE:
                                FirebaseStorage
                                        .getInstance()
                                        .getReferenceFromUrl(content.get("content").toString())
                                        .delete()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.v(TAG, "Failed to delete file when delete message");
                                            }
                                        });
                                break;
                            case Message.TEXT:
                                break;
                        }
                    }
                });
    }
}
