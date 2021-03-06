package com.geopdfviewer.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.geopdfviewer.android.JZActivity.TAG;

/**
 * Created by 54286 on 2018/3/20.
 */

public class mVedioobjAdapter extends RecyclerView.Adapter<mVedioobjAdapter.ViewHolder>{
    private Context mContext;

    private List<mVedioobj> mVedioobjList;

    private mVedioobjAdapter.OnRecyclerItemLongListener mOnItemLong;

    private mVedioobjAdapter.OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        ImageView VedioImage;
        TextView VedioName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            VedioImage = (ImageView) view.findViewById(R.id.vedio_image);
            VedioName = (TextView) view.findViewById(R.id.vedio_txt);



        }
    }
    public mVedioobjAdapter(List<mVedioobj> mVedioobjList) {
        this.mVedioobjList = mVedioobjList;
    }

    @Override
    public mVedioobjAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.vedio_item, parent, false);
        final mVedioobjAdapter.ViewHolder holder = new mVedioobjAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mVedioobj mvedioobj = mVedioobjList.get(position);
                mOnItemClick.onItemClick(v, mvedioobj.getM_path(), position);
                /*MediaPlayer mediaPlayer = MediaPlayer.create(mContext, Uri.parse(mtapeobj.getM_path()));
                mediaPlayer.start();*/
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    mVedioobj mvedioobj = mVedioobjList.get(position);
                    mOnItemLong.onItemLongClick(v, mvedioobj.getM_path());
                    holder.cardView.setCardBackgroundColor(Color.GRAY);
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(mVedioobjAdapter.ViewHolder holder, int position) {
        mVedioobj mvedioobj = mVedioobjList.get(position);
        try {
            String images=mvedioobj.getThumbnailImg();
            Bitmap bitmap= BitmapFactory.decodeFile(images);
            holder.VedioImage.setImageBitmap(bitmap);
            String data;
            data = "????????????: " + mvedioobj.getM_name() + "\n" + "??????: " + mvedioobj.getM_time();
            //+ "\n" + "???????????????: " +
            //        mtape.getM_X() + ", " + mtape.getM_Y()
            holder.VedioName.setText(data);
        }
        catch (Exception e){
            Log.w(TAG, "?????????????????????????????????: " + e.toString());
        }
    }


    @Override
    public int getItemCount() {
        return mVedioobjList.size();
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view,String path);
    }
    public void setOnItemLongClickListener(mVedioobjAdapter.OnRecyclerItemLongListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemLong =  listener;
    }
    public interface OnRecyclerItemClickListener{
        void onItemClick(View view,String path, int position);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }
}

