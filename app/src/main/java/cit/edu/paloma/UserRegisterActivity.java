package cit.edu.paloma;

import android.media.MediaCodec;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userFullNameEdit;
    private EditText emailEdit;
    private EditText passEdit;
    private Button registerButton;
    private Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    );

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

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }

    private void createUser(final String email, String password) {
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(UserRegisterActivity.this, String.format("Cannot create user with email'%s'", email), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return !password.isEmpty();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                String email = emailEdit.getText().toString();
                String password = passEdit.getText().toString();
                String fullName = userFullNameEdit.getText().toString();

                if (fullName.isEmpty()) {
                    fullName = email.substring(0, email.indexOf('.'));
                }

                System.out.printf("%s %s\n", email, fullName);
                if (validateInput(email, password, fullName)) {
                    createUser(email, password);
                }

                break;
        }
    }

    private boolean validateInput(String email, String password, String fullName) {
        boolean isValid = true;

        if (!isValidEmail(email)) {
            emailEdit.setError("Email is invalid");
            isValid = false;
        } else {
            emailEdit.setError(null);
        }

        if (!isValidPassword(password)) {
            passEdit.setError("Password must not be empty");
            isValid = false;
        } else {
            passEdit.setError(null);
        }

        return isValid;
    }
}
