package cit.edu.paloma.datamodals;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class ChatGroup {
    private String groupId;
    private String groupName;
    private HashMap<String, Object> members;
    private List<Object> messages;
    private long timestamp;

    public ChatGroup() {
    }

    public ChatGroup(String groupId, String groupName, HashMap<String, Object> members, List<Object> messages, long timestamp) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = members;
        this.messages = messages;
        this.timestamp = timestamp;
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

    public HashMap<String, Object> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Object> members) {
        this.members = members;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
        this.messages = messages;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupId", this.groupId);
        map.put("groupName", this.groupName);
        map.put("members", this.members);
        map.put("messages", this.messages);
        map.put("timestamp", this.timestamp);
        return map;
    }

    @Exclude
    public void copyFrom(ChatGroup chatGroup) {
        this.groupId = chatGroup.groupId;
        this.groupName = chatGroup.groupName;
        this.members = chatGroup.members;
        this.messages = chatGroup.messages;
        this.timestamp = chatGroup.timestamp;
    }
}
