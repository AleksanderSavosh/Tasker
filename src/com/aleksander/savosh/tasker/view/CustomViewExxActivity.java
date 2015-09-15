package com.aleksander.savosh.tasker.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.aleksander.savosh.tasker.R;

public class CustomViewExxActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "--- === ON CREATE CustomViewExxActivity === ---");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exx_activity);

        findViewById(R.id.exx_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.exx_activity_progress_bar).setVisibility(View.VISIBLE);
            }
        });

//        RelativeLayout layout = new RelativeLayout(this);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT
//        );
//        layout.setLayoutParams(params);
//
//        ColorfulProgressBar progressBar = new ColorfulProgressBar(this);
//        params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT
//        );
//        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//        progressBar.setLayoutParams(params);
//
//        layout.addView(progressBar);
//
//        setContentView(layout);
    }
}
