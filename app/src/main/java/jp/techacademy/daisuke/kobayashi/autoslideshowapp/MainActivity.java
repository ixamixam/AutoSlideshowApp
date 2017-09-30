package jp.techacademy.daisuke.kobayashi.autoslideshowapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.provider.MediaStore;
import android.net.Uri;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    TextView mTimerText;
    double mTimerSec = 0.0;

    Button mStartButton;
    Button mPauseButton;
    Button mResetButton;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mTimerText = (TextView) findViewById(R.id.timer);
        mStartButton = (Button) findViewById(R.id.start_button);
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mResetButton = (Button) findViewById(R.id.reset_button);

        mStartButton.setOnClickListener(startClickListener);
        mPauseButton.setOnClickListener(pauseClickListener);
        mResetButton.setOnClickListener(resetClickListener);


    }

    //Startボタン
    OnClickListener startClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimer == null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimerSec += 0.1;

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTimerText.setText(String.format("%.1f", mTimerSec));
                            }
                        });
                    }
                }, 100, 100);
            }
        }
    };

    //Pauseボタン
    OnClickListener pauseClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    };

    //Resetボタン
    OnClickListener resetClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mTimerSec = 0.0;
            mTimerText.setText(String.format("%.1f", mTimerSec));

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;

            }
        }
    };

}