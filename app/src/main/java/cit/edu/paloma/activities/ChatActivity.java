package cit.edu.paloma.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.UUID;

import cit.edu.paloma.R;
import cit.edu.paloma.adapters.MessagesListAdapter;
import cit.edu.paloma.datamodals.Message;
import cit.edu.paloma.misc.IdentifierGenerator;
import cit.edu.paloma.receivers.OpenFileWithAppReceiver;
import cit.edu.paloma.utils.DateTimeUtils;
import cit.edu.paloma.utils.FirebaseUtils;
import cit.edu.paloma.utils.MessagesAdapterUtils;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.MEDIA_IGNORE_FILENAME;

@SuppressWarnings("VisibleForTests")
public class ChatActivity
        extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";
    public static final String PARAM_GROUP_CHAT_ID = "PARAM_GROUP_CHAT_ID";
    public static final String PARAM_CURRENT_USER_ID = "PARAM_CURRENT_USER_ID";
    public static final String PARAM_GROUP_CHAT_NAME = "PARAM_GROUP_CHAT_NAME";

    private static final int ACTION_REQUEST_GALLERY = 0;
    private static final int ACTION_REQUEST_CAMERA = 1;
    private static final int ACTION_REQUEST_FILE = 2;

    private static final int OPEN_FILE_WITH_APP_RC = 0;

    private Button mSendButton;
    private EditText mMessageEdit;
    private ListView mMessagesList;
    private ActionBar mActionBar;
    private AlertDialog mGroupChatRenameDialog;
    private TextView mEmptyConversationText;
    private ProgressDialog mImageUploadProcessDialog;
    private android.app.LoaderManager mLoaderManager;
    private NotificationManager mNotifyManager;
    private IdentifierGenerator mIdGenerator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        mIdGenerator = new IdentifierGenerator();
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
        mMessagesList.setOnItemLongClickListener(this);

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

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
                    String randomFileName = "Snapshot " + DateTimeUtils.getScreenshotDateTime(System.currentTimeMillis());
                    File file = new File(this.getCacheDir(), randomFileName);
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        file.deleteOnExit();
                    }
                    bitmapsUris.add(Uri.fromFile(file));
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
            uploadImageToFirebase(mIdGenerator.nextInt(), bitmapsUris.get(i));
        }

        for (int i = 0; i < filesUris.size(); i++) {
            uploadFilesToFirebase(mIdGenerator.nextInt(), filesUris.get(i));
        }
    }

    private void uploadImageToFirebase(final int id, final Uri uri) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        final String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);
        final String userId = getIntent().getStringExtra(PARAM_CURRENT_USER_ID);

        mBuilder.setContentTitle("Uploading image")
                .setContentText(getFileName(uri))
                .setProgress(100, 0, true)
                .setSmallIcon(R.drawable.ic_action_send_photo);

        mNotifyManager.notify(id, mBuilder.build());

        final String remoteName = generateRemoteName();

        StorageReference storage = FirebaseStorage
                .getInstance()
                .getReference();

        storage
                .child(groupId + "/images/" + remoteName)
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mBuilder.setProgress(1, 1, false)
                                .setContentTitle("Completed")
                                .setSmallIcon(R.mipmap.ic_completed);

                        synchronized (mNotifyManager) {
                            mNotifyManager.notify(id, mBuilder.build());
                            mIdGenerator.putBackInt(id);
                        }

                        HashMap<String, Object> content = new HashMap<>();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                            content.put("content", taskSnapshot.getDownloadUrl().toString());
                            content.put("filename", getFileName(uri));
                            content.put("remotename", remoteName);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mBuilder.setProgress(1, 1, false)
                                .setContentTitle("Failed")
                                .setContentText(e.getMessage())
                                .setSmallIcon(R.mipmap.ic_failed);
                        synchronized (mNotifyManager) {
                            mNotifyManager.notify(id, mBuilder.build());
                            mIdGenerator.putBackInt(id);
                        }
                    }
                });
    }

    private String generateRemoteName() {
        MessagesListAdapter adapter = (MessagesListAdapter) mMessagesList.getAdapter();
        String remoteName = UUID.randomUUID().toString();
        while (adapter.isDuplicatedName(remoteName)) {
            remoteName = UUID.randomUUID().toString();
        }
        return remoteName;
    }

    private String getFileName(Uri uri) {
        String result = UUID.randomUUID().toString();
        if (uri.getScheme().equals("file")) {
            result = uri.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    Integer nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    cursor.moveToFirst();
                    result = cursor.getString(nameIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return result;
    }

    private void uploadFilesToFirebase(final int id, final Uri uri) {
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        final String groupId = getIntent().getStringExtra(PARAM_GROUP_CHAT_ID);
        final String userId = getIntent().getStringExtra(PARAM_CURRENT_USER_ID);

        mBuilder.setContentTitle("Uploading file")
                .setContentText(getFileName(uri))
                .setProgress(100, 0, true)
                .setSmallIcon(R.drawable.ic_action_send_file);

        mNotifyManager.notify(id, mBuilder.build());

        final String remoteName = UUID.randomUUID().toString();

        StorageReference storage = FirebaseStorage
                .getInstance()
                .getReference();

        storage
                .child(groupId + "/files/" + remoteName)
                .putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mBuilder.setProgress(1, 1, false)
                                .setContentTitle("Completed")
                                .setSmallIcon(R.mipmap.ic_completed);

                        synchronized (mNotifyManager) {
                            mNotifyManager.notify(id, mBuilder.build());
                            mIdGenerator.putBackInt(id);
                        }

                        HashMap<String, Object> content = new HashMap<>();

                        content.put("content", taskSnapshot.getDownloadUrl().toString());
                        content.put("filename", getFileName(uri));
                        content.put("remotename", remoteName);
                        content.put("sender", userId);

                        Message message = new Message(
                                "",
                                groupId,
                                userId,
                                Message.FILE,
                                content,
                                ServerValue.TIMESTAMP
                        );

                        FirebaseUtils
                                .sendMessage(message, null);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mBuilder.setProgress(1, 1, false)
                                .setContentTitle("Failed")
                                .setContentText(e.getMessage())
                                .setSmallIcon(R.mipmap.ic_failed);

                        synchronized (mNotifyManager) {
                            mNotifyManager.notify(id, mBuilder.build());
                            mIdGenerator.putBackInt(id);
                        }
                    }
                });
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
        intent.setType("*/*");

        if (ensurePermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent chooser = Intent.createChooser(intent, "Choose files");
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(content.get("content").toString()));
                startActivity(intent);
                break;
            case Message.FILE:
                Toast.makeText(this, "Starting to download the file", Toast.LENGTH_LONG).show();
                downloadFileFromFirebase(item);
                break;
        }
    }

    private void downloadFileFromFirebase(Message item) {
        if (ensurePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir.exists()) {
                File file = new File(downloadDir, item.getContent().get("filename").toString());

                final NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatActivity.this);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, OPEN_FILE_WITH_APP_RC, new Intent(this, OpenFileWithAppReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setProgress(100, 0, true);
                builder.setSmallIcon(R.drawable.ic_download);
                builder.setContentText("Downloading");
                builder.setSubText(item.getContent().get("filename").toString());
                builder.setContentIntent(pendingIntent);

                final int notificationId = mIdGenerator.nextInt();
                synchronized (mNotifyManager) {
                    mNotifyManager.notify(notificationId, builder.build());
                }

                FirebaseStorage
                        .getInstance()
                        .getReferenceFromUrl(item.getContent().get("content").toString())
                        .getFile(file)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                builder.setProgress(1, 1, false);
                                builder.setContentText("Download completed");
                                builder.setSmallIcon(R.mipmap.ic_completed);
                                synchronized (mNotifyManager) {
                                    mNotifyManager.notify(notificationId, builder.build());
                                    mIdGenerator.putBackInt(notificationId);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                builder.setProgress(1, 1, false);
                                builder.setContentText("Download failed");
                                builder.setSmallIcon(R.mipmap.ic_failed);
                                synchronized (mNotifyManager) {
                                    mNotifyManager.notify(notificationId, builder.build());
                                    mIdGenerator.putBackInt(notificationId);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        MessagesListAdapter adapter = (MessagesListAdapter) parent.getAdapter();
        Message item = adapter.getItem(position);
        HashMap<String, Object> content = item.getContent();

        switch (item.getContentType()) {

        }

        return true;
    }
}
