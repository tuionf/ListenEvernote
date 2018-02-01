package com.example.tuionf.listenevernote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;

public class YinxiangActivity extends AppCompatActivity implements View.OnClickListener,EvernoteLoginFragment.ResultCallback{

    private Button login_auto;
    private static final String TAG = "YinxiangActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yinxiang);

        login_auto = (Button) findViewById(R.id.login_auto);
        login_auto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        EvernoteSession.getInstance().authenticate(this);
    }

    @Override
    public void onLoginFinished(boolean successful) {
        Toast.makeText(this, "授权完成", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onLoginFinished: " );
        startActivity(new Intent(this,MainActivity.class));
    }

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, YinxiangActivity.class));
    }
}
