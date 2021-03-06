package com.geopdfviewer.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by 54286 on 2018/3/20.
 */

public class mPhotobjAdapter extends RecyclerView.Adapter<mPhotobjAdapter.ViewHolder>{
    private static final String TAG = "mPhotobjAdapter";
    private Context mContext;

    private List<mPhotobj> mPhotobjList;

    private mPhotobjAdapter.OnRecyclerItemLongListener mOnItemLong;

    private mPhotobjAdapter.OnRecyclerItemClickListener mOnItemClick;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //private OnRecyclerItemLongListener mOnItemLong = null;
        CardView cardView;
        ImageView PhotoImage;
        TextView PhotoName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            PhotoImage = (ImageView) view.findViewById(R.id.photo_image);
            PhotoName = (TextView) view.findViewById(R.id.photo_txt);



        }
    }
    public mPhotobjAdapter(List<mPhotobj> mPhotobjList) {
        this.mPhotobjList = mPhotobjList;
    }

    @Override
    public mPhotobjAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        final mPhotobjAdapter.ViewHolder holder = new mPhotobjAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mPhotobj poi = mPhotobjList.get(position);
                mOnItemClick.onItemClick(v, poi.getM_path(), position, poi.getM_time());
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLong != null){
                    int position = holder.getAdapterPosition();
                    mPhotobj mphotobj = mPhotobjList.get(position);
                    mOnItemLong.onItemLongClick(v, mphotobj.getM_path(), mphotobj.getM_time());
                    holder.cardView.setCardBackgroundColor(Color.GRAY);
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(mPhotobjAdapter.ViewHolder holder, int position) {
        mPhotobj mphoto = mPhotobjList.get(position);
        //holder.PhotoImage.setImageURI(Uri.parse(mphoto.getM_path()));
        //File outputImage = new File(mphoto.getM_path());
        /*if (Build.VERSION.SDK_INT >= 24){
            holder.PhotoImage.setImageBitmap(getImageThumbnail(mphoto.getM_path(), 100, 120));
        }else {*/
        String path = mphoto.getM_path();
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = DataUtil.getImageThumbnail(path, 100, 120);
            /*int degree = DataUtil.getPicRotate(path);
            if (degree != 0) {
                Matrix m = new Matrix();
                m.setRotate(degree); // ??????angle???
                Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }*/
            holder.PhotoImage.setImageBitmap(bitmap);
        }else {
            Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
            holder.PhotoImage.setImageBitmap(bitmap);
        }


        //}
        String data;
        data = "????????????: " + mphoto.getM_name() + "\n" + "??????: " + mphoto.getM_time();
        // + "\n" + "???????????????: " +
        //mphoto.getM_X() + ", " + mphoto.getM_Y()
        holder.PhotoName.setText(data);
    }


    @Override
    public int getItemCount() {
        return mPhotobjList.size();
    }



    public interface OnRecyclerItemLongListener{
        void onItemLongClick(View view, String path, String time);
    }
    public void setOnItemLongClickListener(mPhotobjAdapter.OnRecyclerItemLongListener listener){
        //Log.w(TAG, "setOnItemLongClickListener: " );
        this.mOnItemLong =  listener;
    }
    public interface OnRecyclerItemClickListener{
        void onItemClick(View view, String path, int position, String time);
    }
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.mOnItemClick =  listener;
    }

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // ????????????????????????????????????????????????bitmap???null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // ?????? false
        // ???????????????
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // ???????????????????????????????????????bitmap?????????????????????options.inJustDecodeBounds ?????? false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // ??????ThumbnailUtils???????????????????????????????????????????????????Bitmap??????
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
