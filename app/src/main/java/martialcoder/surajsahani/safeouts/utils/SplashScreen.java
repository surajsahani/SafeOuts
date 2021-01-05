package martialcoder.surajsahani.safeouts.utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import martialcoder.surajsahani.safeouts.PhoneAuthActivity;
import martialcoder.surajsahani.safeouts.R;


public class SplashScreen<file> extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(7000);
                    Intent intent=new Intent(SplashScreen.this, PhoneAuthActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }


}
