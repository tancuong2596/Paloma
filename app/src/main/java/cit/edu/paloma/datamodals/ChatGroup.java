package cit.edu.paloma.datamodals;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class ChatGroup {
    private String groupId;
    private String groupName;
    private String recentMessage;
    private HashMap<String, Object> members;

    public ChatGroup() {
    }

    public ChatGroup(String groupId, String groupName, String recentMessage, HashMap<String, Object> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.recentMessage = recentMessage;
        if (members == null) {
            members = new HashMap<>();
        }
        this.members = members;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    public HashMap<String, Object> getMembers() {
        if (members == null) {
            members = new HashMap<>();
        }
        return members;
    }

    public void setMembers(HashMap<String, Object> members) {
        if (members == null) {
            members = new HashMap<>();
        }
        this.members = members;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupId", this.groupId);
        map.put("groupName", this.groupName);
        map.put("members", this.members);
        map.put("recentMessage", this.recentMessage);
        return map;
    }

    @Exclude
    public void copyFrom(ChatGroup chatGroup) {
        this.groupId = chatGroup.groupId;
        this.groupName = chatGroup.groupName;
        this.members = chatGroup.members;
        this.recentMessage = chatGroup.recentMessage;
    }
}
