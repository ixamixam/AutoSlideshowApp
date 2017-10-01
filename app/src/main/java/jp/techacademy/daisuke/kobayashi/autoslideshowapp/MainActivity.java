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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.provider.MediaStore;
import android.net.Uri;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private Cursor cursor;
    private Timer mTimer;
    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----許可取得
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

        main();

    }

    private void main(){
        //とりあえず１枚目セット

        // テスト用
        // cursor = null;

        Log.d("ANDROID", "動いてる？");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                setImageview(cursor);
            }
        }

        //----ボタン処理
        Button mStartButton;
        Button mPauseButton;
        Button mResetButton;

        mStartButton = (Button) findViewById(R.id.start_button);
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mResetButton = (Button) findViewById(R.id.reset_button);

        if (cursor != null) {
            mStartButton.setOnClickListener(startClickListener);
            mPauseButton.setOnClickListener(nextClickListener);
            mResetButton.setOnClickListener(previousClickListener);
        }
    }

    //URLの取得
    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursorTemp = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
        cursor = cursorTemp;
    }

    //取得情報のチェック
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                    Log.d("ANDROID", "許可された");
                    main();
                }
                break;
            default:
                break;
        }
    }

    //画像セット
    private void setImageview(Cursor cursor){
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
    }

    //Start・Stopボタン
    OnClickListener startClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimer == null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (cursor.moveToNext()) {
                            cursor.moveToNext();

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setImageview(cursor);
                                }
                            });

                        } else {
                            cursor.moveToFirst();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setImageview(cursor);
                                }
                            });
                        }
                    }
                }, 1000, 2000);
            }else {
                mTimer.cancel();
                mTimer = null;
            }
        }
    };

    //Nextボタン
    OnClickListener nextClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimer == null) {
                if (cursor.moveToNext()) {
                    cursor.moveToNext();
                    setImageview(cursor);
                } else {
                    cursor.moveToFirst();
                    setImageview(cursor);
                }
            }
        }
    };

    //Previousボタン
    OnClickListener previousClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTimer == null) {
                if (cursor.moveToPrevious()) {
                    cursor.moveToPrevious();
                    setImageview(cursor);
                } else {
                    cursor.moveToLast();
                    setImageview(cursor);
                }
            }
        }
    };

}