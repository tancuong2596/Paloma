package cit.edu.paloma.activities;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import cit.edu.paloma.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";

    private Button sendButton;
    private EditText messageEdit;
    private ListView messagesList;
    private ActionBar actionBar;
    private AlertDialog mGroupChatRenameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    private void initViews() {
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        messageEdit = (EditText) findViewById(R.id.message_edit);

        messagesList = (ListView) findViewById(R.id.messages_list);

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

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra(PARAM_ACTION_BAR_TITLE));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                break;
        }
    }
}
