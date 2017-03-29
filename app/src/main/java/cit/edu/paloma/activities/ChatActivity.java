package cit.edu.paloma.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.UUID;

import cit.edu.paloma.R;
import cit.edu.paloma.adapters.MessagesListAdapter;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.utils.FirebaseUtils;
import cit.edu.paloma.utils.ImgurUtils;
import cit.edu.paloma.utils.MessagesAdapterUtils;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

@SuppressWarnings("VisibleForTests")
public class ChatActivity
        extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";
    public static final String PARAM_GROUP_CHAT_ID = "PARAM_GROUP_CHAT_ID";
    public static final String PARAM_CURRENT_USER_ID = "PARAM_CURRENT_USER_ID";
    public static final String PARAM_GROUP_CHAT_NAME = "PARAM_GROUP_CHAT_NAME";

    private static final int ACTION_REQUEST_GALLERY = 0;
    private static final int ACTION_REQUEST_CAMERA = 1;
    private static final int ACTION_REQUEST_FILE = 2;

    private Button mSendButton;
    private EditText mMessageEdit;
    private ListView mMessagesList;
    private ActionBar mActionBar;
    private AlertDialog mGroupChatRenameDialog;
    private TextView mEmptyConversationText;
    private ProgressDialog mImageUploadProcessDialog;
    private android.app.LoaderManager mLoaderManager;
    private volatile NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);

        mEmptyConversationText = (TextView) findViewById(R.id.empty_conversation_text);

        mMessageEdit = (EditText) findViewById(R.id.message_edit);

        mMessagesList = (ListView) findViewById(R.id.messages_list);

        String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);
        mMessagesList.setAdapter(MessagesAdapterUtils.findAdapterByGroupId(groupId, this));
        mMessagesList.setOnItemClickListener(this);

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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

        ArrayList<Uri> bitmapsUris = new ArrayList<>();
        ArrayList<Uri> filesUris = new ArrayList<>();

        switch (requestCode) {
            case ACTION_REQUEST_CAMERA:
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    // todo: upload file captured by camera
                }
                break;
            case ACTION_REQUEST_GALLERY:
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        bitmapsUris.add(item.getUri());
                    }
                } else if (data.getData() != null) {
                    bitmapsUris.add(data.getData());
                }
                break;
            case ACTION_REQUEST_FILE:
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        filesUris.add(item.getUri());
                    }
                } else if (data.getData() != null) {
                    filesUris.add(data.getData());
                }
                break;
        }


        for (int i = 0; i < bitmapsUris.size(); i++) {
            uploadImageToFileBase(i, bitmapsUris.get(i));
        }

        for (int i = 0; i < filesUris.size(); i++) {
            uploadFilesToFirebase(i, filesUris.get(i));
        }
    }

    private void uploadImageToFileBase(final int i, final Uri uri) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("Uploading image")
                .setContentText(uri.getPath())
                .setSmallIcon(R.drawable.ic_action_send_photo);

        mNotifyManager.notify(i, mBuilder.build());

        final String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);
        final String userId = getIntent().getStringExtra(PARAM_CURRENT_USER_ID);

        try {
            FirebaseUtils
                    .uploadFile(uri,
                            groupId,
                            userId,
                            null,
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mBuilder.setProgress(1, 1, false)
                                            .setContentTitle("Completed")
                                            .setSmallIcon(R.mipmap.ic_completed);
                                    synchronized (mNotifyManager) {
                                        mNotifyManager.notify(i, mBuilder.build());
                                    }

                                    HashMap<String, Object> content = new HashMap<>();
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                                        content.put("content", taskSnapshot.getDownloadUrl().toString());
                                        content.put("filename", getFileName(uri.getPath()));
                                        content.put("sender", userId);
                                        content.put("height", bitmap.getHeight());
                                        content.put("width", bitmap.getWidth());

                                        Message message = new Message(
                                                "",
                                                groupId,
                                                userId,
                                                Message.IMAGE,
                                                content,
                                                ServerValue.TIMESTAMP
                                        );

                                        FirebaseUtils
                                                .sendMessage(message, null);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mBuilder.setProgress(1, 1, false)
                                            .setContentTitle("Failed")
                                            .setContentText(e.getMessage())
                                            .setSmallIcon(R.mipmap.ic_failed);
                                    synchronized (mNotifyManager) {
                                        mNotifyManager.notify(i, mBuilder.build());
                                    }
                                }
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(String path) {
        String[] tokens = path.split("/");
        return tokens[tokens.length - 1];
    }

    private void uploadFilesToFirebase(int i, Uri uri) {
        String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);
        String userId = getIntent().getStringExtra(PARAM_CURRENT_USER_ID);
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
                chooseFileSource();
                break;
            case R.id.action_send_image:
                chooseImageSource();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseFileSource() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("file/*");

        if (ensurePermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent chooser = Intent.createChooser(intent, "Choose a file");
            startActivityForResult(chooser, ACTION_REQUEST_FILE);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessagesListAdapter adapter = (MessagesListAdapter) parent.getAdapter();
        Message item = adapter.getItem(position);
        HashMap<String, Object> content = item.getContent();

        switch (item.getContentType()) {
            case Message.IMAGE:
                WebView webView = new WebView(this);
                webView.loadUrl(content.get("content").toString());
                break;
            case Message.FILE:
                // todo: implement to handle when the file message is clicked
                break;
        }
    }
}
