package com.appyware.filters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Wejfrey_510 on 1/6/2015.
 */
public class Splash extends Activity {


    private Animation animation;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //logo = (ImageView) findViewById(R.id.logo_img);
        textView = (TextView) findViewById(R.id.textView);

        if (savedInstanceState == null) {
            flyIn();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                endSplash();
            }
        }, 1500);
    }

    private void flyIn() {
       /* animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(animation);*/

        animation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation);
        textView.startAnimation(animation);

    }

    private void endSplash() {
       /* animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back);
        logo.startAnimation(animation);*/

        animation = AnimationUtils.loadAnimation(this, R.anim.app_name_animation_back);
        textView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}