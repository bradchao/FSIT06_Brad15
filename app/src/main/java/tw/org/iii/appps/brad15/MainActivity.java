package tw.org.iii.appps.brad15;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mesg;
    private MyReceiver myReceiver;
    private ImageView img;
    private Bitmap bmp;
    private UIHandler handler;
    private boolean isSaveStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        }else{
            isSaveStorage = true;
        }

        handler = new UIHandler();
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("brad");
        registerReceiver(myReceiver, filter);

        mesg = findViewById(R.id.mesg);
        img = findViewById(R.id.img);
        Log.v("brad", "debug1");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isSaveStorage = true;
        }
    }

    public void test1(View view) {
        new Thread(){
            @Override
            public void run() {
                try {
                    // http => Android 8+ => useClearTextTraffic = true
                    URL url = new URL("http://www.bradchao.com");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                    String line = null; StringBuffer sb = new StringBuffer();
                    while ( (line = reader.readLine()) != null){
                        Log.v("brad", line);
                        sb.append(line);
                    }
                    reader.close();

                    Intent intent = new Intent("brad");
                    intent.putExtra("data", sb.toString());
                    sendBroadcast(intent);    // Context: Activity, Service, Application

                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }

    public void test2(View view) {
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("https://s1.yimg.com/uu/api/res/1.2/Q_0Jcl9rnvrjciz3oHMGgA--~B/Zmk9dWxjcm9wO2N3PTExMjE7ZHg9Nzk7Y2g9NjI2O2R5PTA7dz0zOTI7aD0zMDg7Y3I9MTthcHBpZD15dGFjaHlvbg--/https://media-mbst-pub-ue1.s3.amazonaws.com/creatr-uploaded-images/2019-08/ad741a90-b65a-11e9-bfae-29bc2afa3515");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    handler.sendEmptyMessage(0);

                }catch (Exception e){

                }
            }
        }.start();
    }

    public void test3(View view) {
        if (!isSaveStorage) return;


        


    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            img.setImageBitmap(bmp);

        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("brad", "OK");

            String data = intent.getStringExtra("data");
            mesg.setText(data);

        }
    }

}
