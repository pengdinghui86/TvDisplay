package com.esc.screendisplay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.esc.screendisplay.utils.ActivityCollector;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
