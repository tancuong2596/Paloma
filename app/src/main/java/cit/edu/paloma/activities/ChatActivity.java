package cit.edu.paloma.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import cit.edu.paloma.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    private EditText messageEdit;
    private ListView messagesList;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                break;
        }
    }
}
