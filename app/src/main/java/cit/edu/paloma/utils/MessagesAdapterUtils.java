package cit.edu.paloma.utils;

import android.content.Context;

import java.util.HashMap;

import cit.edu.paloma.adapters.MessagesListAdapter;

/**
 * Created by charlie on 3/20/17.
 */

public class MessagesAdapterUtils {
    private static HashMap<String, MessagesListAdapter> mAdapterByGroupId = new HashMap<>();

    public static MessagesListAdapter findAdapterByGroupId(String groupId, Context context) {
        if (!mAdapterByGroupId.containsKey(groupId)) {
            mAdapterByGroupId.put(groupId, new MessagesListAdapter(context, groupId));
        }
        return mAdapterByGroupId.get(groupId);
    }

    public static void clear() {
        for (String groupId : mAdapterByGroupId.keySet()) {
            mAdapterByGroupId.get(groupId).detachAllEventListeners();
        }
        mAdapterByGroupId.clear();
    }
}
