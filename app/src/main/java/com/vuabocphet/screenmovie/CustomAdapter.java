package com.vuabocphet.screenmovie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.vuabocphet.screenmovie.util.DensityUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.vuabocphet.screenmovie.Home.cmy;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHodel> {
    private ArrayList<VideoModel> list;
    private Home context;
    private final static int FADE_DURATION = 100;
    int br;
    File bf;

    public CustomAdapter(ArrayList<VideoModel> list, Home context, int br) {
        this.list = list;
        this.context = context;
        this.br = br;
    }

    @NonNull
    @Override
    public CustomViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemviewcustom, parent, false);

        return new CustomViewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHodel holder, final int position) {
        final VideoModel model = list.get(position);


        Log.e("TAG", model.getUrlimg());
        holder.img.setVideoPath(model.getUrlimg());
        holder.img.setOnPreparedListener(PreparedListener);



        customAnimation(holder.itemView);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(12f);
        holder.img.setBackground(drawable);


        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "ios_1.ttf");

        holder.filename.setTypeface(typeface);
        holder.filetime.setTypeface(typeface);
        holder.sizefile.setTypeface(typeface);
        holder.date.setTypeface(typeface);
        holder.date.setText(model.getDate());
        holder.filename.setText("Tên video : " + model.getName());
        holder.filetime.setText("Thời gian : " + model.getTime());
        holder.sizefile.setText("Kích thước : " + model.getSize());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                show2(position, model);

            }
        });

    }

    private void bss(final int position) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + cmy.get(position));
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, cmy.get(position));
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ lên"));
    }


    private void fn(final int position, String newname, VideoModel model) {
        File file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + cmy.get(position));
        File dir = file.getParentFile();
        File from = new File(dir, file.getName());
        File to = new File(dir, newname);
        from.renameTo(to);
        context.fhjbv();
    }

    private void bdd(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa !");

        builder.setIcon(R.drawable.ic_delete);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bf = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + cmy.get(position));
                bf.delete();
                list.remove(position);
                notifyItemRemoved(position);

                notifyItemRangeChanged(position,getItemCount());
                Log.e("POSITION",position+"");
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void customAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        view.startAnimation(animation);
    }


    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                m.setVolume(0f, 0f);
                m.setLooping(false);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void show2(final int i, final VideoModel model) {
        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_content_normal, null);
        dialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = context.getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(context, 16f);
        params.bottomMargin = DensityUtil.dp2px(context, 8f);
        contentView.setLayoutParams(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        TextView play;
        TextView rename;
        TextView share;
        TextView url;
        TextView delete;
        TextView close;

        play = dialog.findViewById(R.id.play);
        rename = dialog.findViewById(R.id.rename);
        share = dialog.findViewById(R.id.share);
        url = dialog.findViewById(R.id.url);
        delete = dialog.findViewById(R.id.delete);
        close = dialog.findViewById(R.id.close);

        //chạy video

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/" + cmy.get(i));
                intent.setDataAndType(uri, "video/*");
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        //đổi tên file
        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final Dialog dialogname = new Dialog(context, R.style.BottomDialog);
                View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_rename, null);
                dialogname.setContentView(contentView);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
                params.width = context.getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(context, 16f);
                params.bottomMargin = DensityUtil.dp2px(context, 8f);
                contentView.setLayoutParams(params);
                dialogname.setCanceledOnTouchOutside(true);
                dialogname.getWindow().setGravity(Gravity.BOTTOM);
                dialogname.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);

                final EditText edtname = dialogname.findViewById(R.id.edtname);
                edtname.setHint("ví dụ:videoname.mp4");
                edtname.setText(model.getName());
                dialogname.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = edtname.getText().toString();
                        if (name.equals("")) {
                            edtname.setError(context.getString(R.string.errnull));
                            return;
                        }
                        if (name.equals(model.getName())) {
                            Toast.makeText(context, "Không thay đổi", Toast.LENGTH_SHORT).show();
                        }
                        if (!name.endsWith(".mp4")) {
                            edtname.setError(context.getString(R.string.errstyle));
                            return;
                        }

                        fn(i, name, model);
                        dialogname.dismiss();
                    }
                });

                dialogname.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogname.cancel();
                    }
                });
                dialogname.show();

            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bss(i);
                dialog.dismiss();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bdd(i);
                dialog.dismiss();
            }
        });

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + cmy.get(i));

                Toast.makeText(context, file.getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog.show();
    }


}
