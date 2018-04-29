package ie.wit.collegeplanner.activities;
/*
References
https://inducesmile.com/android/how-to-create-android-spl
http://abhiandroid.com/programming/splashscreen
 */
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ie.wit.collegeplanner.R;

public class Splash extends AppCompatActivity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splash.this,Subjects.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
