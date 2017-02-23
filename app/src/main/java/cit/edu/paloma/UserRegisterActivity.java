package cit.edu.paloma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText userFullNameEdit;
    private EditText emailEdit;
    private EditText passEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        initViews();
    }

    private void initViews() {
        userFullNameEdit = (EditText) findViewById(R.id.register_full_name_edit);
        emailEdit = (EditText) findViewById(R.id.register_email_edit);
        passEdit = (EditText) findViewById(R.id.register_pass_edit);
    }


}
