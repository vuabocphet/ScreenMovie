package com.vuabocphet.screenmovie;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class CustomViewHodel extends RecyclerView.ViewHolder {
    public VideoView img;
    public TextView filename;
    public TextView filetime;
    public TextView sizefile;

    public TextView date;


    public CustomViewHodel(View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.img);
        filename = itemView.findViewById(R.id.filename);
        filetime = itemView.findViewById(R.id.filetime);
        sizefile = itemView.findViewById(R.id.sizefile);

        date = itemView.findViewById(R.id.date);
    }
}
