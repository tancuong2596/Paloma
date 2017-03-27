package cit.edu.paloma.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cit.edu.paloma.R;

public class ImagePreviewActivity extends AppCompatActivity {

    private static final String PARAM_ACTION_BAR_TITLE = "PARAM_ACTION_BAR_TITLE";
    private static final String PARAM_IMAGE_NAME = "PARAM_IMAGE_NAME";
    private static final String PARAM_IMAGE_URL = "PARAM_IMAGE_URL";
    private static final String PARAM_IMAGE_WIDTH = "PARAM_IMAGE_WIDTH";
    private static final String PARAM_IMAGE_HEIGHT = "PARAM_IMAGE_HEIGHT";
    private static final String PARAM_IMAGE_SENDER_FULLNAME = "PARAM_IMAGE_SENDER_FULLNAME";
    private static final String PARAM_IMAGE_SENDER_EMAIL = "PARAM_IMAGE_SENDER_EMAIL";

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
}
