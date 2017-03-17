package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;

import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.utils.FirebaseUtils;

/**
 * Created by charlie on 3/15/17.
 */

public class MessagesListAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Message> mMessagesList;
    private final HashMap<String, Integer> mMessageKeyIndexMap;

    public MessagesListAdapter(@NonNull Context context) {
        this.mContext = context;
        this.mMessagesList = new ArrayList<>();
        this.mMessageKeyIndexMap = new HashMap<>();
        setupMessagesChildEventListener();
    }

    private void setupMessagesChildEventListener() {
        FirebaseUtils
                .getMessagesRef()
                .orderByChild("timestamp")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        Message message = dataSnapshot.getValue(Message.class);
                        if (prevChildKey == null) {
                            mMessagesList.add(0, message);
                            mMessageKeyIndexMap.put(message.getMessageId(), 0);
                        } else {
                            int prevChildIndex = mMessageKeyIndexMap.get(prevChildKey);
                            mMessagesList.add(prevChildIndex + 1, message);
                            mMessageKeyIndexMap.put(message.getMessageId(), prevChildIndex + 1);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                        Message message = dataSnapshot.getValue(Message.class);

                        int childIndex = mMessageKeyIndexMap.get(message.getMessageId());
                        mMessagesList.set(childIndex, message);

                        notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Message message = dataSnapshot.getValue(Message.class);

                        int childIndex = mMessageKeyIndexMap.get(message.getMessageId());
                        mMessagesList.remove(childIndex);
                        mMessageKeyIndexMap.remove(message.getMessageId());

                        notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getCount() {
        return mMessagesList.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        return convertView;
    }
}
