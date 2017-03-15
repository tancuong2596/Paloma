package cit.edu.paloma.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import cit.edu.paloma.datamodals.Message;

/**
 * Created by charlie on 3/15/17.
 */

public class MessagesListAdapter extends ArrayAdapter<Message> {
    public MessagesListAdapter(@NonNull Context context) {
        super(context, 0, new ArrayList<Message>());
    }
}
