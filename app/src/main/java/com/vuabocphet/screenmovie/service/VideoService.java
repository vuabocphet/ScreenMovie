package com.vuabocphet.screenmovie.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.vuabocphet.screenmovie.CustomAdapter;
import com.vuabocphet.screenmovie.Home;
import com.vuabocphet.screenmovie.R;
import com.vuabocphet.screenmovie.VideoModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class VideoService extends Service {

    private WindowManager mWindowManager;
    private View mChatHeadView;
    private WindowManager.LayoutParams params;
    private boolean a = true;
    private ImageView tvShow;
    private ImageView tvPlay;
    private ImageView tvMic;
    private ImageView close;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    public VideoService() {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_video, null);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_dialog_in);
        final Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_dialog_out);
        mChatHeadView.startAnimation(animation);
        int api = Integer.valueOf(android.os.Build.VERSION.SDK);
        Log.e("Tinh", api + "");
        if (api >= 26) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = -50;
        params.y = 100;


        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);


        tvShow = mChatHeadView.findViewById(R.id.tv_show);
        tvPlay = mChatHeadView.findViewById(R.id.tv_play);
        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VideoService.this, "PLAYS", Toast.LENGTH_SHORT).show();
            }
        });
        tvMic = mChatHeadView.findViewById(R.id.tv_mic);
        close = mChatHeadView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VideoService.this, Home.class));
                stopSelf();

            }
        });


        preferences = getSharedPreferences("aaa", MODE_PRIVATE);
        final boolean mic = preferences.getBoolean("MIC", true);

        Log.e("MIC", mic + "");

        if (mic) {
            tvMic.setImageResource(R.drawable.mic);

        } else {
            tvMic.setImageResource(R.drawable.nomic);
        }

        tvMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean micc = preferences.getBoolean("MIC", true);
                editor = preferences.edit();
                if (micc) {
                    Toast.makeText(VideoService.this, "Tắt tiếng", Toast.LENGTH_SHORT).show();
                    tvMic.setImageResource(R.drawable.nomic);
                    editor.putBoolean("MIC", false);
                    editor.commit();
                } else {
                    Toast.makeText(VideoService.this, "Micro", Toast.LENGTH_SHORT).show();
                    tvMic.setImageResource(R.drawable.mic);
                    editor.putBoolean("MIC", true);
                    editor.commit();
                }

            }
        });
        Log.e("W", tvShow.getWidth() + "");
        Log.e("H", tvShow.getHeight() + "");
        final ViewGroup.LayoutParams aaa = mChatHeadView.getLayoutParams();
        tvShow.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TEST", "TEST1");
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:

                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.

                            if (a) {

                                tvShow.setImageResource(R.drawable.no);
                                tvPlay.startAnimation(animation);
                                tvMic.startAnimation(animation);
                                tvPlay.setVisibility(View.VISIBLE);
                                tvMic.setVisibility(View.VISIBLE);
                                close.setVisibility(View.VISIBLE);

                                a = false;
                            } else {
                                tvShow.setImageResource(R.drawable.video);
                                tvPlay.startAnimation(animation1);
                                tvMic.startAnimation(animation1);
                                tvPlay.setVisibility(View.GONE);
                                tvMic.setVisibility(View.GONE);
                                close.setVisibility(View.GONE);
                                a = true;
                            }

                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TEST", "TEST2");
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        mWindowManager.updateViewLayout(mChatHeadView, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_NOT_STICKY;

    }
}

