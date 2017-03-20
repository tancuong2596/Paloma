package cit.edu.paloma.utils;

import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

    public static void sendMessage(Message message, @Nullable OnCompleteListener onCompleteListener) {
        DatabaseReference newMessageRef = FirebaseUtils
                .getMessagesRef()
                .push();

        String messageKey = newMessageRef.getKey();

        message.setMessageId(messageKey);

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(message.getGroupChatId() + "/" + messageKey, message.toMap());

        FirebaseUtils
                .getChatGroupsRef()
                .child(message.getGroupChatId())
                .child("recentMessage")
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + ": " + message.getContent());

        FirebaseUtils
                .getMessagesRef()
                .updateChildren(updateChildren);
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
        HashMap<String, Object> groupMembersUid = new HashMap<>();

        for (Object[] member : members) {
            User user = (User) member[1];
            groupMembersUid.put(user.getUserId(), user.getAvatar());
        }

        ChatGroup chatGroup = new ChatGroup(
                groupChatId,
                "",
                "",
                new HashMap<String, Object>()
        );

        HashMap<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(groupChatId, chatGroup.toMap());

        FirebaseUtils
                .getChatGroupsRef()
                .updateChildren(updateChildren);

        return chatGroupRef;
    }
}
