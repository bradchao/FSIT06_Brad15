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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
    private File downloadDir;

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
            initSDCard();
        }

        handler = new UIHandler();
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("brad");
        registerReceiver(myReceiver, filter);

        mesg = findViewById(R.id.mesg);
        img = findViewById(R.id.img);


    }

    private void initSDCard(){
        downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isSaveStorage = true;
            initSDCard();

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

        new Thread(){
            @Override
            public void run() {
                try{
                    URL url = new URL("https://pdfmyurl.com/?url=https://www.gamer.com.tw");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    File downloadFile = new File(downloadDir, "gamer.pdf");
                    FileOutputStream fout = new FileOutputStream(downloadFile);

                    byte[] buf = new byte[4096*4096];
                    BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
                    int len = -1;
                    while ( (len = bin.read(buf)) != -1){
                        fout.write(buf,0, len);
                    }
                    fout.flush();
                    fout.close();
                    bin.close();

                    Log.v("brad", "finish");
                }catch (Exception e){

                }
            }
        }.start();



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
