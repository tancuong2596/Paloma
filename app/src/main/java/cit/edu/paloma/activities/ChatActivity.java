package cit.edu.paloma.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import cit.edu.paloma.R;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.utils.FirebaseUtils;
import cit.edu.paloma.utils.ImgurUtils;
import cit.edu.paloma.utils.MessagesAdapterUtils;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

public class ChatActivity
        extends AppCompatActivity
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Object> {
    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";
    public static final String PARAM_GROUP_CHAT_ID = "PARAM_GROUP_CHAT_ID";
    public static final String PARAM_CURRENT_USER_ID = "PARAM_CURRENT_USER_ID";
    public static final String PARAM_GROUP_CHAT_NAME = "PARAM_GROUP_CHAT_NAME";

    private static final int ACTION_REQUEST_GALLERY = 0;
    private static final int ACTION_REQUEST_CAMERA = 1;
    private static final int IMAGES_UPLOADING_ID = 0;

    private Button mSendButton;
    private EditText mMessageEdit;
    private ListView mMessagesList;
    private ActionBar mActionBar;
    private AlertDialog mGroupChatRenameDialog;
    private TextView mEmptyConversationText;
    private ProgressDialog mImageUploadProcessDialog;
    private android.app.LoaderManager mLoaderManager;

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
                .setTitle("Name your group")
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

        mImageUploadProcessDialog = new ProgressDialog(this);

        mLoaderManager = getLoaderManager();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setTitle(getIntent().getStringExtra(PARAM_ACTION_BAR_TITLE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_chat_menu, menu);
        return true;
    }

    private boolean ensurePermission(String... permissions) {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(ChatActivity.this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission);
            }
        }

        String[] strArray = new String[notGrantedPermissions.size()];
        notGrantedPermissions.toArray(strArray);

        if (!notGrantedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    ChatActivity.this,
                    strArray,
                    ACTION_REQUEST_CAMERA
            );
        }

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(ChatActivity.this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Cannot grant " + permission);
                return false;
            }
        }

        return true;
    }

    private void chooseImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setType("image/png|image/jpg|image/jpeg|image/gif|image/apng");

                        if (ensurePermission(android.Manifest.permission.CAMERA,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Intent chooser = Intent.createChooser(intent, "Choose a Picture");
                            startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
                        }
                        break;
                    case 1:
                        if (ensurePermission(android.Manifest.permission.CAMERA)) {
                            Intent getCameraImage = new Intent(ACTION_IMAGE_CAPTURE);
                            startActivityForResult(getCameraImage, ACTION_REQUEST_CAMERA);
                        } else {
                            Log.v(TAG, "Cannot grant permission to access camera");
                        }
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

        if (resultCode != RESULT_OK) {
            return;
        }

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        switch (requestCode) {
            case ACTION_REQUEST_CAMERA:
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    bitmaps.add(bitmap);
                }
                break;
            case ACTION_REQUEST_GALLERY:
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), item.getUri());
                            bitmaps.add(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (data.getData() != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        bitmaps.add(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        if (!bitmaps.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("bitmaps", bitmaps);
            mLoaderManager.restartLoader(IMAGES_UPLOADING_ID, bundle, this).forceLoad();
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
            case R.id.action_send_file:
                break;
            case R.id.action_send_image:
                chooseImageSource();
                break;
        }
        return super.onOptionsItemSelected(item);
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

                HashMap<String, Object> textContent = new HashMap<>();
                textContent.put("content", mMessageEdit.getText().toString());

                Message newMessage = new Message(
                        "",
                        getIntent().getStringExtra(PARAM_GROUP_CHAT_ID),
                        getIntent().getStringExtra(PARAM_CURRENT_USER_ID),
                        Message.TEXT,
                        textContent,
                        ServerValue.TIMESTAMP
                );

                FirebaseUtils
                        .sendMessage(newMessage, null);

                mMessageEdit.setText(null);

                scrollToEnd();
                break;
        }
    }

    @Override
    public Loader<Object> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Object>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mImageUploadProcessDialog.setMessage("Uploading chosen images...");
                mImageUploadProcessDialog.setIndeterminate(true);
                mImageUploadProcessDialog.show();
                forceLoad();
            }

            private String uploadImageToImgur(@NonNull Bitmap bitmap) throws Exception {
                String imageLink = null;

                Response response =
                        ImgurUtils.uploadBase64Photo(ImgurUtils.encodeBitmapToBase64(bitmap));

                if (response.isSuccessful()) {
                    JSONObject data = new JSONObject(response.body().string()).getJSONObject("data");
                    imageLink = data.getString("link");
                } else {
                    throw new Exception("Cannot upload the image");
                }

                return imageLink;
            }


            @Override
            public Object loadInBackground() {
                ArrayList<HashMap<String, Object>> uploadedImagesLinks = new ArrayList<>();
                ArrayList<Bitmap> bitmaps = null;

                if (args != null) {
                    bitmaps = args.getParcelableArrayList("bitmaps");
                }

                if (bitmaps == null) {
                    return null;
                }

                for (Bitmap bitmap : bitmaps) {
                    String link = null;
                    try {
                        link = uploadImageToImgur(bitmap);
                        HashMap<String, Object> imageContent = new HashMap<>();
                        imageContent.put("content", link);
                        imageContent.put("height", bitmap.getHeight());
                        imageContent.put("width", bitmap.getWidth());
                        uploadedImagesLinks.add(imageContent);
                    } catch (Exception e) {
                    }
                }

                return uploadedImagesLinks;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case IMAGES_UPLOADING_ID:
                mImageUploadProcessDialog.hide();
                ArrayList<HashMap<String, Object>> uploadedImagesLinks = (ArrayList<HashMap<String, Object>>) data;
                for (HashMap<String, Object> imageLink : uploadedImagesLinks) {

                    Message newMessage = new Message(
                            "",
                            getIntent().getStringExtra(PARAM_GROUP_CHAT_ID),
                            getIntent().getStringExtra(PARAM_CURRENT_USER_ID),
                            Message.IMAGE,
                            imageLink,
                            ServerValue.TIMESTAMP
                    );

                    FirebaseUtils
                            .sendMessage(newMessage, null);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
