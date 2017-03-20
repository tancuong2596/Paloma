package cit.edu.paloma.activities;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

import cit.edu.paloma.R;
import cit.edu.paloma.adapters.MessagesListAdapter;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.utils.FirebaseUtils;
import cit.edu.paloma.utils.MessagesAdapterUtils;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";
    public static final String PARAM_GROUP_CHAT_ID = "PARAM_GROUP_CHAT_ID";
    public static final String PARAM_CURRENT_USER_ID = "PARAM_CURRENT_USER_ID";
    public static final String PARAM_GROUP_CHAT_NAME = "PARAM_GROUP_CHAT_NAME";

    private static final String STATE_MESSAGES_LIST_GROUP_BY_ID = "STATE_MESSAGES_LIST_GROUP_BY_ID";

    private Button mSendButton;
    private EditText mMessageEdit;
    private ListView mMessagesList;
    private ActionBar mActionBar;
    private AlertDialog mGroupChatRenameDialog;
    private TextView mEmptyConversationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    private void initViews() {
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);

        mEmptyConversationText = (TextView) findViewById(R.id.empty_conversation_text);

        mMessageEdit = (EditText) findViewById(R.id.message_edit);

        mMessagesList = (ListView) findViewById(R.id.messages_list);

        String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);
        mMessagesList.setAdapter(MessagesAdapterUtils.findAdapterByGroupId(groupId, this));

        mGroupChatRenameDialog = new AlertDialog
                .Builder(this, R.style.DialogTheme)
                .setView(getLayoutInflater().inflate(R.layout.input_box_dialog, null))
                .setTitle("Name your conversation")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setTitle(getIntent().getStringExtra(PARAM_ACTION_BAR_TITLE));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scrollToEnd() {
        mMessagesList.post(new TimerTask() {
            @Override
            public void run() {
                mMessagesList.setSelection(mMessagesList.getCount() - 1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:

                if (mMessageEdit.getText().toString().isEmpty()) {
                    return;
                }

                Message newMessage = new Message(
                        "",
                        getIntent().getStringExtra(PARAM_GROUP_CHAT_ID),
                        getIntent().getStringExtra(PARAM_CURRENT_USER_ID),
                        Message.TEXT,
                        mMessageEdit.getText().toString(),
                        ServerValue.TIMESTAMP
                );

                FirebaseUtils
                        .sendMessage(newMessage, null);

                mMessageEdit.setText(null);

                scrollToEnd();
                break;
        }
    }
}
