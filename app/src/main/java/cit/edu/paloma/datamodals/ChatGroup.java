package cit.edu.paloma.datamodals;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by charlie on 3/12/17.
 */

public class ChatGroup {
    private String groupId;
    private List<String> members;
    private List<Object> messages;

    public ChatGroup() {
    }

    public ChatGroup(String groupId, List<String> members, List<Object> messages) {
        this.groupId = groupId;
        this.members = members;
        this.messages = messages;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatGroup{" +
                "groupId='" + groupId + '\'' +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
