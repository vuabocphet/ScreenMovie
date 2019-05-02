package com.vuabocphet.screenmovie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.vuabocphet.screenmovie.service.VideoService;
import com.vuabocphet.screenmovie.util.DensityUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.app.Notification.GROUP_ALERT_SUMMARY;

public class Home extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private CustomAdapter customAdapter;
    private ArrayList<VideoModel> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static int QUALITY_2160P = 2160;
    private static int QUALITY_1080P = 1080;
    private static int QUALITY_720P = 720;
    private static int QUALITY_480P = 480;
    private static final String TAG = "C";
    private static final int REQUEST_CODE = 1000;
    private int cms;
    private MediaProjectionManager cp;
    private static int cw = 720;
    private static int ch = 1280;
    private MediaProjection cme;
    private VirtualDisplay cmv;
    private MediaProjectionCallback cmx;
    private MediaRecorder cmr;
    private static final SparseIntArray cor = new SparseIntArray();
    private static final int crp = 10;

    private RelativeLayout settinga;


    String cf;
    ListView clv;
    String cxy = "";
    FloatingActionButton cxv;
    public static List<String> cmy;
    File file;
    File clt;
    long cld;
    String dte;
    private static final String YES_ACTION = "YES_ACTION";
    private NotificationManager notificationManager;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private String uri = "";
    private TextView appname;


    static {
        cor.append(Surface.ROTATION_0, 90);
        cor.append(Surface.ROTATION_90, 0);
        cor.append(Surface.ROTATION_180, 270);
        cor.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (isMyServiceRunning(VideoService.class)) {

            Log.e("RUN",isMyServiceRunning(VideoService.class)+"");
            stopService(new Intent(this,VideoService.class));


        }else {
            Log.e("RUN",isMyServiceRunning(VideoService.class)+"");

        }
        MobileAds.initialize(this, getString(R.string.app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        Animation slideTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_top);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        settinga = findViewById(R.id.setting);
        settinga.startAnimation(slideTop);
        RelativeLayout rlt=findViewById(R.id.rlt);
        settinga.startAnimation(slideTop);
        rlt.startAnimation(slideTop);
        settinga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                settinga.setVisibility(View.INVISIBLE);
                setting();
            }
        });
        appname = findViewById(R.id.appname);
        appname.setTypeface(Typeface.createFromAsset(getAssets(), "ios_1.ttf"));

        recyclerView = findViewById(R.id.recyclerview);
        cxv = findViewById(R.id.fav);
        cxv.startAnimation(slideUp);
        cxy = "s";
        //creating the adapter
        sharedPreferences = getSharedPreferences("aaa", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        uri = sharedPreferences.getString("xx", "");
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Screen Recording");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
            }
        }
        jqjdd();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        cw = display.getWidth();
        ch = display.getHeight();
        cxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cxy.equals("s")) {
                    ctgh();
                    isQ();
                } else if (cxy.equals("t")) {
                    notificationManager.cancel(1);
                    cxy = "s";
                    cxv.setImageResource(R.drawable.ic_recode);
                    ctgdd();
                    fhjbv();
                    cfhgw();
                }

            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        cms = metrics.densityDpi;
        cmr = new MediaRecorder();
        cp = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        jfcfdfh(getIntent());

    }


    private void jqjdd() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            fhjbv();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(Home.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void cf() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        cf = formatter.format(now);
    }

    @SuppressLint("DefaultLocale")
    public static String fdfdfbg(long seconds) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(seconds),
                TimeUnit.MILLISECONDS.toMinutes(seconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(seconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));
    }

    public static String fdfhcvq(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public void fhjbv() {
        list.clear();
        cmy = new ArrayList<String>();
        File directory = Environment.getExternalStorageDirectory();
        file = new File(directory + "/Screen Recording");
        File lista[] = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String cf) {
                // TODO Auto-generated method stub
                if (cf.contains(".mp4")) {
                    return true;
                }
                return false;
            }
        });

        for (int i = 0; i < lista.length; i++) {
            cmy.add(lista[i].getName());


            String filepath = Environment.getExternalStorageDirectory() + "/Screen Recording/" + lista[i].getName();
            Log.e("URL", filepath);
            File file = new File(filepath);
            long length = file.length();
            if (length < 1024) {
                clt = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + lista[i].getName());
                clt.delete();
            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/" + lista[i].getName()));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
                cld = Long.parseLong(time);
                dte = date;
            }
            list.add(new VideoModel(filepath, lista[i].getName(), fdfdfbg(cld), fdfhcvq(length), formatMediaDate(dte)));
        }
        customAdapter = new CustomAdapter(list, this, R.layout.itemviewcustom);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(customAdapter);





    }

    public static String formatMediaDate(String date) {
        String formattedDate = "";
        try {
            Date inputDate = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault()).parse(date);
            formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(inputDate);
        } catch (Exception e) {
            Log.w(TAG, "error parsing date: ", e);
            try {
                Date inputDate = new SimpleDateFormat("yyyy MM dd", Locale.getDefault()).parse(date);
                formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(inputDate);
            } catch (Exception ex) {
                Log.e(TAG, "error parsing date: ", ex);
            }
        }
        return formattedDate;
    }

    private Intent fflvv() {
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public void fflnke() {
        Intent yesIntent = fflvv();
        yesIntent.setAction(YES_ACTION);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Screen Recoder App")
                .setContentText("Đang quay")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setUsesChronometer(true)
                .setVibrate(null)
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                .setGroup("My group")
                .setGroupSummary(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_close,
                        "t",
                        PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT)));
        notificationManager.notify(notificationId, mBuilder.build());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        jfcfdfh(intent);
        super.onNewIntent(intent);
    }

    private void jfcfdfh(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case YES_ACTION:
                    notificationManager.cancel(1);
                    cxy = "s";
                    cxv.setImageResource(R.drawable.ic_recode);
                    ctgdd();
                    fhjbv();
                    cfhgw();
                    break;
            }
        }
    }

    public void ctgh() {
        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(Home.this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (Home.this, Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        crp);
            }
        } else {
            jfhft();
            ceendf();
        }
    }

    public void ctgdd() {
        cmr.stop();
        cmr.reset();
        Log.v(TAG, "Stopping Recording");
        fffhqeo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            notificationManager.cancel(1);
            fhjbv();
            cxy = "s";
            cxv.setImageResource(R.drawable.ic_recode);

            return;
        } else {
            cxy = "t";
            cxv.setImageResource(R.drawable.ic_clear);
            fflnke();
            cddvn();
            cfdft();
        }
        cmx = new MediaProjectionCallback();
        cme = cp.getMediaProjection(resultCode, data);
        cme.registerCallback(cmx, null);
        cmv = createVirtualDisplay();
        cmr.start();
    }

    public void cddvn() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void cfdft() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void cfhgw() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

    }

    private void ceendf() {
        if (cme == null) {
            startActivityForResult(cp.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        cmv = createVirtualDisplay();
        cmr.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return cme.createVirtualDisplay("C",
                cw, ch, cms,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                cmr.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void jfhft() {

        cf();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int QUALITY = sharedPreferences.getInt("QUALITY", width);
        int h=0;
        int w = 0;
        try {
            CamcorderProfile profile = null;
            Log.e("TEST-AAA", QUALITY + "");
            if (QUALITY == width) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);


            }
            if (QUALITY == QUALITY_2160P) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_2160P);

            }
            if (QUALITY == QUALITY_1080P) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
            }

            if (QUALITY == QUALITY_720P) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            }

            if (QUALITY == QUALITY_480P) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            }
            int i=sharedPreferences.getInt("SIZE",6000000);
            int FPS=sharedPreferences.getInt("FPS",30);
            boolean is=sharedPreferences.getBoolean("MIC",true);
            Log.e("IS",is+"");
            if (is){
                cmr.setAudioSource(MediaRecorder.AudioSource.MIC);
            }

            cmr.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            cmr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            cmr.setOutputFile(Environment.getExternalStorageDirectory() + "/Screen Recording" + "/Screen Recording " + cf + ".mp4");
            cmr.setVideoSize(width, height);
            cmr.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            if (is){
                cmr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            }
            cmr.setVideoEncodingBitRate(profile.videoBitRate);
            Log.e("RATE",profile.videoBitRate+"");
            cmr.setVideoFrameRate(FPS);
            cmr.setVideoEncodingBitRate(i);
            cmr.setCaptureRate(20);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = cor.get(rotation + 90);
            cmr.setOrientationHint(orientation);
            cmr.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case crp: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            cmr.stop();
            cmr.reset();
            Log.v(TAG, "Recording Stopped");
            cme = null;
            fffhqeo();
        }
    }

    private void fffhqeo() {
        if (cmv == null) {
            return;
        }
        cmv.release();

        jfcdv();
    }

    private void jfcdv() {
        if (cme != null) {
            cme.unregisterCallback(cmx);
            cme.stop();
            cme = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jfcdv();
    }





    private void setting() {
        final Dialog dialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_setting, null);
        dialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = this.getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialogSetting_Animation);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                settinga.startAnimation(slideUp);
                settinga.setVisibility(View.VISIBLE);
            }
        });


        TextView txtPixel;
        final TextView pixel;
        TextView txtQuality;
        final TextView quality;
        final TextView txtRatio;
        final TextView ratio;
        TextView txtAudio;
        final TextView audio;
        TextView txtClick;
        Switch clcik;
        TextView txtExit;

        txtPixel = (TextView) dialog.findViewById(R.id.txtPixel);
        pixel = (TextView) dialog.findViewById(R.id.pixel);
        txtQuality = (TextView) dialog.findViewById(R.id.txtQuality);
        quality = (TextView) dialog.findViewById(R.id.quality);
        txtRatio = (TextView) dialog.findViewById(R.id.txtRatio);
        ratio = (TextView) dialog.findViewById(R.id.ratio);
        txtAudio = (TextView) dialog.findViewById(R.id.txtAudio);
        audio = (TextView) dialog.findViewById(R.id.audio);
        txtClick = (TextView) dialog.findViewById(R.id.txtClick);
        clcik = (Switch) dialog.findViewById(R.id.clcik);
        txtExit = (TextView) dialog.findViewById(R.id.txtExit);
        txtExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                settinga.startAnimation(slideUp);

                settinga.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int QUALITY = sharedPreferences.getInt("QUALITY", width);



        if (QUALITY >= QUALITY_2160P) {
            pixel.setText("3840 * 2160");
        }

        if (QUALITY < QUALITY_2160P && QUALITY > QUALITY_720P) {
            pixel.setText("1920 * 1080");
        }

        if (QUALITY < QUALITY_1080P && QUALITY > QUALITY_480P) {
            pixel.setText("1280 * 720");
        }

        if (QUALITY <= QUALITY_480P) {
            pixel.setText("720 * 480");
        }

        int i=sharedPreferences.getInt("SIZE",6000000);
        if(i==6000000){

            quality.setText("6Mbps");

        }

        if(i==1000000){

            quality.setText("1Mbps");
        }


        if(i==4000000){

            quality.setText("4Mbps");
        }


        if(i==8000000){

            quality.setText("8Mbps");
        }
        if(i==16000000){

            quality.setText("16Mbps");
        }

        if(i==24000000){

            quality.setText("24Mbps");
        }

        if(i==32000000){
            quality.setText("32Mbps");
        }

        if(i==40000000){
            quality.setText("40Mbps");
        }

        int FPS=sharedPreferences.getInt("FPS",30);

        if (FPS==30){
            ratio.setText(getResources().getText(R.string.fps_30));
        }

        if (FPS==24){
            ratio.setText(getResources().getText(R.string.fps_24));
        }


        if (FPS==15){
            ratio.setText(getResources().getText(R.string.fps_15));
        }

        boolean is=sharedPreferences.getBoolean("MIC",true);

        if (is){
            audio.setText(getString(R.string.on_mic));
        }else {
            audio.setText(getString(R.string.off_mic));
        }



        txtPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogpixel(pixel);

            }
        });

        txtQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogquaity(quality);

            }
        });

        txtRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_fps(ratio);
            }
        });

        txtAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_mic(audio);
            }
        });


        dialog.show();


    }

    private void dialogpixel(final TextView pixel) {
        final Dialog dialogqulity = new Dialog(Home.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(Home.this).inflate(R.layout.item_pixel, null);
        dialogqulity.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(Home.this, 16f);
        params.bottomMargin = DensityUtil.dp2px(Home.this, 8f);
        contentView.setLayoutParams(params);
        dialogqulity.setCanceledOnTouchOutside(true);
        dialogqulity.getWindow().setGravity(Gravity.BOTTOM);
        dialogqulity.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        TextView close;
        TextView txt2K;
        TextView txtFull;
        TextView txtHD;
        TextView txt480;
        txt2K = (TextView) dialogqulity.findViewById(R.id.txt2K);
        txtFull = (TextView) dialogqulity.findViewById(R.id.txtFull);
        txtHD = (TextView) dialogqulity.findViewById(R.id.txtHD);
        txt480 = (TextView) dialogqulity.findViewById(R.id.txt480);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int QUALITY = sharedPreferences.getInt("QUALITY", width);
        Log.e("PIXEL", height + "*" + width);

        if (width < QUALITY_2160P) {
            txt2K.setClickable(false);
            txt2K.setTextColor(getResources().getColor(R.color.xam));
        } else {
            txt2K.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putInt("QUALITY", QUALITY_2160P);
                    editor.commit();
                    dialogqulity.dismiss();
                    pixel.setText("3840 * 2160");
                }
            });
        }
        if (width < QUALITY_1080P) {
            txtFull.setClickable(false);
            txtFull.setTextColor(getResources().getColor(R.color.xam));
        } else {
            txtFull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putInt("QUALITY", QUALITY_1080P);
                    editor.commit();
                    dialogqulity.dismiss();
                    pixel.setText("1920 * 1080");
                }
            });

        }
        if (width < QUALITY_720P) {
            txtHD.setClickable(false);
            txtHD.setTextColor(getResources().getColor(R.color.xam));
        } else {
            txtHD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putInt("QUALITY", QUALITY_720P);
                    editor.commit();
                    dialogqulity.dismiss();
                    pixel.setText("1280 * 720");
                }
            });
        }
        if (QUALITY >= QUALITY_2160P) {
            txt2K.setTextColor(getResources().getColor(R.color.blue));
            txt2K.setText(" > " + txt2K.getText().toString());
        }

        if (QUALITY < QUALITY_2160P && QUALITY > QUALITY_720P) {
            txtFull.setTextColor(getResources().getColor(R.color.blue));
            txtFull.setText(" > " + txtFull.getText().toString());
        }

        if (QUALITY < QUALITY_1080P && QUALITY > QUALITY_480P) {
            txtHD.setTextColor(getResources().getColor(R.color.blue));
            txtHD.setText(" > " + txtHD.getText().toString());
        }

        if (QUALITY <= QUALITY_480P) {
            txt480.setTextColor(getResources().getColor(R.color.blue));
            txt480.setText(" > " + txt480.getText().toString());
        }

        editor = sharedPreferences.edit();

        txt480.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("QUALITY", QUALITY_480P);
                editor.commit();
                dialogqulity.dismiss();
                pixel.setText("720 * 480");
            }
        });

        close = dialogqulity.findViewById(R.id.close_1);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogqulity.cancel();

            }
        });
        dialogqulity.show();
    }


    private void dialogquaity(final TextView quality) {

        final Dialog dialog = new Dialog(Home.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(Home.this).inflate(R.layout.item_quality, null);
        dialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(Home.this, 16f);
        params.bottomMargin = DensityUtil.dp2px(Home.this, 8f);
        contentView.setLayoutParams(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        TextView txt40Mbps;
        TextView txt32Mbps;
        TextView txt24Mbps;
        TextView txt16Mbps;
        TextView txt8Mbps;
        TextView txt6Mbps;
        TextView txt4Mbps;
        TextView txt1Mbps;
        TextView close1;

        txt40Mbps = (TextView) dialog.findViewById(R.id.txt40Mbps);
        txt32Mbps = (TextView) dialog.findViewById(R.id.txt32Mbps);
        txt24Mbps = (TextView) dialog.findViewById(R.id.txt24Mbps);
        txt16Mbps = (TextView) dialog.findViewById(R.id.txt16Mbps);
        txt8Mbps = (TextView) dialog.findViewById(R.id.txt8Mbps);
        txt6Mbps = (TextView) dialog.findViewById(R.id.txt6Mbps);
        txt4Mbps = (TextView) dialog.findViewById(R.id.txt4Mbps);
        txt1Mbps = (TextView) dialog.findViewById(R.id.txt1Mbps);
        close1 = (TextView) dialog.findViewById(R.id.close_1);

        int i=sharedPreferences.getInt("SIZE",6000000);
        if(i==6000000){
            txt6Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt6Mbps.setText(" > " +txt6Mbps.getText().toString());
            quality.setText(txt6Mbps.getText().toString());
        }

        if(i==1000000){
            txt1Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt1Mbps.setText(" > " +txt1Mbps.getText().toString());
        }


        if(i==4000000){
            txt4Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt4Mbps.setText(" > " +txt4Mbps.getText().toString());
        }


        if(i==8000000){
            txt6Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt6Mbps.setText(" > " +txt6Mbps.getText().toString());
        }
        if(i==16000000){
            txt16Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt16Mbps.setText(" > " +txt16Mbps.getText().toString());
        }

        if(i==24000000){
            txt24Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt24Mbps.setText(" > " +txt24Mbps.getText().toString());
        }

        if(i==32000000){
            txt32Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt32Mbps.setText(" > " +txt32Mbps.getText().toString());
        }

        if(i==40000000){
            txt40Mbps.setTextColor(getResources().getColor(R.color.blue));
            txt40Mbps.setText(" > " +txt40Mbps.getText().toString());
        }




        clcikText(dialog, quality, txt1Mbps, 1000000, txt1Mbps.getText().toString());
        clcikText(dialog, quality, txt4Mbps, 4000000, txt4Mbps.getText().toString());
        clcikText(dialog, quality, txt8Mbps, 8000000, txt8Mbps.getText().toString());
        clcikText(dialog, quality, txt16Mbps, 16000000, txt16Mbps.getText().toString());
        clcikText(dialog, quality, txt24Mbps, 24000000, txt24Mbps.getText().toString());
        clcikText(dialog, quality, txt32Mbps, 32000000, txt32Mbps.getText().toString());
        clcikText(dialog, quality, txt40Mbps, 40000000, txt40Mbps.getText().toString());
        clcikText(dialog, quality, txt6Mbps, 6000000, txt6Mbps.getText().toString());
        dialog.show();

        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }


    private void clcikText(final Dialog dialog, final TextView txt, TextView view, final int i, final String s) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.putInt("SIZE", i);
                editor.commit();
                txt.setText(s);
                dialog.dismiss();
            }
        });

    }


    private void dialog_fps(TextView fps){

        final Dialog dialog = new Dialog(Home.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(Home.this).inflate(R.layout.item_fps, null);
        dialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(Home.this, 16f);
        params.bottomMargin = DensityUtil.dp2px(Home.this, 8f);
        contentView.setLayoutParams(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
         TextView txt15fps;
         TextView txt24fps;
         TextView txt30fps;
         TextView close1;

        txt15fps = (TextView) dialog.findViewById(R.id.txt15fps);
        txt24fps = (TextView) dialog.findViewById(R.id.txt24fps);
        txt30fps = (TextView) dialog.findViewById(R.id.txt30fps);
        close1 = (TextView) dialog.findViewById(R.id.close_1);


        int FPS=sharedPreferences.getInt("FPS",30);

        if (FPS==30){
            txt30fps.setText(" > " + getResources().getText(R.string.fps_30));
            txt30fps.setTextColor(getResources().getColor(R.color.blue));
        }

        if (FPS==24){
            txt24fps.setText(" > " + getResources().getText(R.string.fps_24));
            txt24fps.setTextColor(getResources().getColor(R.color.blue));
        }


        if (FPS==15){
            txt15fps.setText(" > " + getResources().getText(R.string.fps_15));
            txt15fps.setTextColor(getResources().getColor(R.color.blue));
        }
        click_fps(dialog,txt15fps,fps,txt15fps.getText().toString(),15);
        click_fps(dialog,txt24fps,fps,txt24fps.getText().toString(),24);
        click_fps(dialog,txt30fps,fps,txt30fps.getText().toString(),30);
        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();

    }


    private void click_fps(final Dialog dialog, TextView view, final TextView txt, final String s, final int i){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.putInt("FPS", i);
                editor.commit();
                txt.setText(s);
                dialog.dismiss();
            }
        });
    }

    private void dialog_mic(final TextView mic){
        final Dialog dialog = new Dialog(Home.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(Home.this).inflate(R.layout.item_mic, null);
        dialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(Home.this, 16f);
        params.bottomMargin = DensityUtil.dp2px(Home.this, 8f);
        contentView.setLayoutParams(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
         TextView txtonmic;
         TextView txtoffmic;
         TextView close1;

        txtoffmic = (TextView) dialog.findViewById(R.id.txtoffmic);
        txtonmic = (TextView) dialog.findViewById(R.id.txtonmic);
        close1 = (TextView) dialog.findViewById(R.id.close_1);
        boolean is=sharedPreferences.getBoolean("MIC",true);
        if (is){
            txtonmic.setText(" > " + txtonmic.getText().toString());
            txtonmic.setTextColor(getResources().getColor(R.color.blue));
        }else{
            txtoffmic.setText(" > " + txtoffmic.getText().toString());
            txtoffmic.setTextColor(getResources().getColor(R.color.blue));
        }


        txtoffmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor=sharedPreferences.edit();
                editor.putBoolean("MIC",false);
                editor.commit();
                mic.setText(getString(R.string.off_mic));
                dialog.dismiss();
            }
        });
        txtonmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor=sharedPreferences.edit();
                editor.putBoolean("MIC",true);
                editor.commit();
                mic.setText(getString(R.string.on_mic));
                dialog.dismiss();
            }
        });

        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


    private void isQ(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
    }

    private void initializeView() {

          Intent intent=new Intent(Home.this, VideoService.class);
          intent.putExtra("MIC",sharedPreferences.getBoolean("MIC",true));
                startService(intent);


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}
