package cit.edu.paloma.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cit.edu.paloma.R;

public class ImagePreviewActivity extends AppCompatActivity {

    public static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";
    public static final String PARAM_IMAGE_NAME = "PARAM_IMAGE_NAME";
    public static final String PARAM_IMAGE_URL = "PARAM_IMAGE_URL";
    public static final String PARAM_IMAGE_WIDTH = "PARAM_IMAGE_WIDTH";
    public static final String PARAM_IMAGE_HEIGHT = "PARAM_IMAGE_HEIGHT";
    public static final String PARAM_IMAGE_SENDER_ID = "PARAM_IMAGE_SENDER_ID";

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setTitle(getIntent().getStringExtra(PARAM_ACTION_BAR_TITLE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image_preview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_image_info:

                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
