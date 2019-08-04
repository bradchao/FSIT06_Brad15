package tw.org.iii.appps.brad15;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mesg;
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("brad");
        registerReceiver(myReceiver, filter);

        mesg = findViewById(R.id.mesg);
    }

    public void test1(View view) {
        new Thread(){
            @Override
            public void run() {
                try {
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("brad", "OK");

            String data = intent.getStringExtra("data");
            mesg.setText(data);

        }
    }

}
