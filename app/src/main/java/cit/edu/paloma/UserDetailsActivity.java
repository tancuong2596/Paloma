package cit.edu.paloma;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.squareup.picasso.OkHttpDownloader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cit.edu.paloma.datamodals.User;

import static android.provider.MediaStore.*;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTION_REQUEST_CAMERA = 0;
    private static final int ACTION_REQUEST_GALLERY = 1;
    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private Button saveUserProfileButton;
    private EditText fullNameEdit;
    private EditText emailEdit;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private ImageView avatarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        saveUserProfileButton = (Button) findViewById(R.id.save_user_profile_button);
        fullNameEdit = (EditText) findViewById(R.id.detail_full_name_edit);
        emailEdit = (EditText) findViewById(R.id.detail_email_edit);
        maleRadio = (RadioButton) findViewById(R.id.male_radio);
        femaleRadio = (RadioButton) findViewById(R.id.female_radio);
        avatarImage = (ImageView) findViewById(R.id.avatar_image);

        saveUserProfileButton.setOnClickListener(this);
        maleRadio.setOnClickListener(this);
        femaleRadio.setOnClickListener(this);
    }

    private void openAvatarPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");

                        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
                        startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
                        break;
                    case 1:
                        Intent getCameraImage = new Intent(ACTION_IMAGE_CAPTURE);
                        startActivityForResult(getCameraImage, ACTION_REQUEST_CAMERA);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case ACTION_REQUEST_CAMERA:
                if (data != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    avatarImage.setImageBitmap(bitmap);
                }
                break;
            case ACTION_REQUEST_GALLERY:
                Uri uri = data.getData();
                try {
                    bitmap = Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageInBase64 = baos.toByteArray();

        Log.v(TAG, imageInBase64.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_user_profile_button:
                openAvatarPicker();
//                user = new User(
//                        emailEdit.getText().toString(),
//                        fullNameEdit.getText().toString(),
//                        ImgurUtils.upload()
//                );
                break;
        }
    }
}
