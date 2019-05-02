package com.vuabocphet.screenmovie;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Screen extends AppCompatActivity {


    private ImageView img;
    private TextView txt;
    private TextView txt1;
    private Animation anim, amAnimation, animation, animation1;
    private ImageView click;
    private ProgressBar load;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        mapped();
        Typeface font = Typeface.createFromAsset(getAssets(), "font_1.ttf");
        anim = AnimationUtils.loadAnimation(this, R.anim.animation_1);
        amAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_2);
        animation = AnimationUtils.loadAnimation(this, R.anim.animation_3);

        txt.setTypeface(font);
        txt1.setTypeface(font);
        click.setAnimation(anim);
        img.setAnimation(amAnimation);
        txt.setAnimation(amAnimation);
        txt1.setAnimation(amAnimation);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.startAnimation(animation);
                load.setVisibility(View.VISIBLE);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Screen.this, Home.class));
                        finish();
                    }
                }, 1300);
            }
        });

    }

    private void mapped() {
        img = findViewById(R.id.img);
        txt = findViewById(R.id.txt);
        txt1 = findViewById(R.id.txt1);
        click = findViewById(R.id.click);
        load = findViewById(R.id.load);
    }
}
