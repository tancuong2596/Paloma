package cit.edu.paloma.datamodals;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by charlie on 2/27/17.
 */

@IgnoreExtraProperties
public class User {
    private String userId;
    private String email;
    private String fullName;
    private String avatar;
    private String recentMessage;
    private boolean isOnline;
    private List<Object> friends;

    public User(String userId, String email, String fullName, String avatar, String recentMessage,
                boolean isOnline, List<Object> friends) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.recentMessage = recentMessage;
        this.isOnline = isOnline;
        this.friends = friends;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<Object> getFriends() {
        return friends;
    }

    public void setFriends(List<Object> friends) {
        this.friends = friends;
    }
}
