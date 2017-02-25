package cit.edu.paloma;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserRegisterActivity.class.getSimpleName();
    private EditText userFullNameEdit;
    private EditText emailEdit;
    private EditText passEdit;
    private Button registerButton;
    private Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    );
    private ProgressDialog globalDialog;
    private ProgressDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        initViews();

        waitingDialog = new ProgressDialog(this, R.style.WaitingTheme);
        waitingDialog.setTitle("Please wait");
        waitingDialog.setMessage("Creating account");
        waitingDialog.setCancelable(false);
        waitingDialog.setIndeterminate(true);
    }


    private void initViews() {
        userFullNameEdit = (EditText) findViewById(R.id.register_full_name_edit);

        emailEdit = (EditText) findViewById(R.id.register_email_edit);

        passEdit = (EditText) findViewById(R.id.register_pass_edit);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }

    private String getUserName() {
        String email = emailEdit.getText().toString();

        String fullName = userFullNameEdit.getText().toString();

        if (fullName.isEmpty()) {
            fullName = email.substring(0, email.indexOf('.'));
        }

        return fullName;
    }

    private void sendConfirmationEmail(FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "User has not signed in");
            return;
        }
        user
                .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            new AlertDialog.Builder(UserRegisterActivity.this, R.style.WaitingTheme)
                                    .setTitle("Successful")
                                    .setIcon(R.mipmap.ic_completed)
                                    .setMessage("A confirmation email has been sent.\nPlease check your email.")
                                    .setNegativeButton("Close", null)
                                    .show();

                        } else {
                            new AlertDialog.Builder(UserRegisterActivity.this, R.style.WaitingTheme)
                                    .setTitle("Problem Occurred")
                                    .setIcon(R.mipmap.ic_failed)
                                    .setMessage("User was successfully created.\n" +
                                            "But the email cannot be sent.\n" +
                                            "Please contact the technicians for assistance")
                                    .setNegativeButton("Close", null)
                                    .show();
                        }
                    }
                });
    }

    private void createUser(final String email, String password) {
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        waitingDialog.hide();
                        if (!task.isSuccessful()) {
                            String message = "Cannot create user";
                            Exception e = task.getException();
                            if (e != null) {
                                message = e.getMessage();
                            }
                            new AlertDialog.Builder(UserRegisterActivity.this, R.style.WaitingTheme)
                                    .setTitle("Failed")
                                    .setIcon(R.mipmap.ic_failed)
                                    .setMessage(message)
                                    .setNegativeButton("Close", null)
                                    .show();
                        } else {
                            sendConfirmationEmail(FirebaseAuth.getInstance().getCurrentUser());
                        }
                    }
                });

    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
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
                    waitingDialog.show();
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
