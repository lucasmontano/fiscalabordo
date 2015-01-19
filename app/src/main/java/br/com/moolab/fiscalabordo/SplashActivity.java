package br.com.moolab.fiscalabordo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SplashActivity extends ActionBarActivity {

    @InjectView(R.id.clouds)
    ImageView mClouds;

    @InjectView(R.id.bus)
    ImageView mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        Animation left = AnimationUtils.loadAnimation(this, R.anim.view_transition_out_right);
        mClouds.startAnimation(left);

        Animation top = AnimationUtils.loadAnimation(this, R.anim.bus);
        mBus.startAnimation(top);
    }

    @OnClick(R.id.go)
    public void go(View view) {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
