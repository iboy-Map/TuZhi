package com.geopdfviewer.android;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.geopdfviewer.android.CameraUtils.RequestCode.FLAG_REQUEST_CAMERA_IMAGE;
import static com.geopdfviewer.android.CameraUtils.RequestCode.FLAG_REQUEST_CAMERA_VIDEO;
import static com.geopdfviewer.android.CameraUtils.RequestCode.FLAG_REQUEST_SYSTEM_VIDEO;

/**
 * 兴趣点显示页面
 * 用于显示兴趣点
 *
 * @author  李正洋
 *
 * @since   1.1
 */
public class singlepoi extends AppCompatActivity{

    private static final String TAG = "singlepoi";
    private String POIC;
    private String name;
    private EditText editText_name;
    private EditText editText_des;
    private final static int REQUEST_CODE_PHOTO = 42;
    private final static int REQUEST_CODE_TAPE = 43;
    private final static int TAKE_PHOTO = 41;
    Uri imageUri;
    Spinner type_spinner;
    float m_lat, m_lng;
    private int POITYPE = -1;
    private String DMXH;
    private String DMP;
    private String DML;
    private TextView edit_XH;
    private EditText edit_DY;
    private EditText edit_MC;
    private EditText edit_BZMC;
    private EditText edit_XZQMC;
    private EditText edit_XZQDM;
    private EditText edit_SZDW;
    private EditText edit_SCCJ;
    private EditText edit_GG;
    private TextView edit_qydm;
    private EditText edit_lbdm;
    private EditText edit_bzmc;
    private EditText edit_cym;
    private EditText edit_jc;
    private EditText edit_bm;
    private EditText edit_dfyz;
    private EditText edit_zt;
    private EditText edit_dmll;
    private EditText edit_dmhy;
    private EditText edit_lsyg;
    private EditText edit_dlstms;
    private EditText edit_zlly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepoi);
        //声明ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        POITYPE = intent.getIntExtra("type", -1);
        Log.w(TAG, "onCreate: " + POITYPE);

        textView_photonum = (TextView) findViewById(R.id.txt_photonumshow);
        textView_photonum.setVisibility(View.VISIBLE);
        textViewShowNum = (TextView) findViewById(R.id.txt_tapenumshow);
        textViewShowNum.setVisibility(View.VISIBLE);
            setTitle("兴趣点信息");
            POIC = intent.getStringExtra("POIC");
            type_spinner = (Spinner) findViewById(R.id.type_selection);
            type_spinner.setVisibility(View.VISIBLE);
            Log.w(TAG, "onCreate: 0 ");

        refreshForCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    int showNum = 0;
    List<bt> bms;
    PointF pt0 = new PointF();
    boolean ges = false;
    TextView textView_photonum;
    String str = "";

    private TextView textView_tapenum;

    private void refreshForCreate(){
        TextView txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name.setVisibility(View.VISIBLE);
        TextView txt_time = (TextView) findViewById(R.id.txt_time);
        txt_time.setVisibility(View.VISIBLE);
        TextView txt_des = (TextView) findViewById(R.id.txt_des);
        txt_des.setVisibility(View.VISIBLE);
        TextView txt_type = (TextView) findViewById(R.id.txt_type);
        txt_type.setVisibility(View.VISIBLE);
        TextView txt_photonum = (TextView) findViewById(R.id.txt_photonum);
        txt_photonum.setVisibility(View.VISIBLE);
        TextView txt_tapenum = (TextView) findViewById(R.id.txt_tapenum);
        txt_tapenum.setVisibility(View.VISIBLE);
        TextView txt_loc = (TextView) findViewById(R.id.txt_loc);
        txt_loc.setVisibility(View.VISIBLE);

        List<POI> pois = LitePal.where("poic = ?", POIC).find(POI.class);
        List<MTAPE> tapes = LitePal.where("poic = ?", POIC).find(MTAPE.class);
        List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
        final List<MPHOTO> photos = LitePal.where("poic = ?", POIC).find(MPHOTO.class);

        //
        String[] strings = getResources().getStringArray(R.array.Type);
        for (int i = 0; i < strings.length; i++) {
            Log.w(TAG, "refresh: " + strings[i]);
            if (strings[i].equals(pois.get(0).getType())) type_spinner.setSelection(i);
        }
        //

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = type_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                str = type_spinner.getSelectedItem().toString();
            }
        });
        getBitmap(photos);
        Log.w(TAG, "pois: " + pois.size() + "\n");
        Log.w(TAG, "tapes1: " + pois.get(0).getTapenum() + "\n");
        Log.w(TAG, "photos1: " + pois.get(0).getPhotonum() + "\n");
        Log.w(TAG, "tapes: " + tapes.size() + "\n");
        Log.w(TAG, "photos: " + photos.size());
        POI poi1 = new POI();
        if (tapes.size() != 0) poi1.setTapenum(tapes.size());
        else poi1.setToDefault("tapenum");
        if (photos.size() != 0) {
            poi1.setPhotonum(photos.size());
            final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
            /*imageView.setVisibility(View.VISIBLE);
            String path = photos.get(0).getPath();
            File file = new File(path);
            try {
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    int degree = DataUtil.getPicRotate(path);
                    if (degree != 0) {
                        Matrix m = new Matrix();
                        m.setRotate(degree); // 旋转angle度
                        Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    }
                    imageView.setImageBitmap(bitmap);
                }else {
                    Drawable drawable = MyApplication.getContext().getResources().getDrawable(R.drawable.imgerror);
                    BitmapDrawable bd = (BitmapDrawable) drawable;
                    Bitmap bitmap = Bitmap.createBitmap(bd.getBitmap(), 0, 0, bd.getBitmap().getWidth(), bd.getBitmap().getHeight());
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, 80, 120,
                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    imageView.setImageBitmap(bitmap);
                }
            }catch (IOException e){
                Log.w(TAG, e.toString());
            }*/
            if (photos.size() >= 1) {
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float distanceX = 0;
                        float distanceY = 0;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //第一个手指按下
                                pt0.set(event.getX(0), event.getY(0));
                                Log.w(TAG, "onTouchdown: " + event.getX());
                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                Log.w(TAG, "ACTION_POINTER_DOWN");
                                Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                //第二个手指按下
                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                Log.w(TAG, "onTouchdown: " + event.getX());
                                Log.w(TAG, "ACTION_POINTER_DOWN");
                                Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                break;
                            case MotionEvent.ACTION_UP:
                                //最后一个手指抬起
                                ges = false;
                                Log.w(TAG, "onTouchup: " + event.getX());
                                Log.w(TAG, "getPointerId: " + event.getPointerId(0));
                                distanceX = event.getX(0) - pt0.x;
                                distanceY = event.getY(0) - pt0.y;
                                Log.w(TAG, "onTouch: " + distanceX);
                                if (Math.abs(distanceX) > Math.abs(distanceY) & Math.abs(distanceX) > 200 & Math.abs(distanceY) < 100) {
                                    if (distanceX > 0) {
                                        Log.w(TAG, "bms.size : " + bms.size());
                                        showNum++;
                                        if (showNum > bms.size() - 1) {
                                            showNum = 0;
                                            imageView.setImageBitmap(bms.get(0).getM_bm());
                                        } else {
                                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                        }
                                    } else {
                                        showNum--;
                                        if (showNum < 0) {
                                            showNum = bms.size() - 1;
                                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                        } else {
                                            imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                        }
                                    }
                                    Log.w(TAG, "同时抬起的手指数量: " + event.getPointerCount());
                                    Log.w(TAG, "手指id: " + event.getActionIndex());
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (event.getPointerCount() == 3) {
                                    Log.w(TAG, "3指滑动");

                                }
                                else if (event.getPointerCount() == 4) {
                                    if (!ges) {
                                        Log.w(TAG, "4指滑动");
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
                                        dialog.setTitle("提示");
                                        dialog.setMessage("确认删除图片吗?");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                List<POI> pois1 = LitePal.where("poic = ?", POIC).find(POI.class);
                                                if (pois1.get(0).getPhotonum() > 0) {
                                                    textView_photonum.setText(Integer.toString(pois1.get(0).getPhotonum() - 1));
                                                    POI poi = new POI();
                                                    poi.setPhotonum(pois1.get(0).getPhotonum() - 1);
                                                    poi.updateAll("poic = ?", POIC);
                                                    LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, bms.get(showNum).getM_path());
                                                    bms.remove(showNum);
                                                    if (showNum > pois1.get(0).getPhotonum() - 1) {
                                                        if (bms.size() > 0) imageView.setImageBitmap(bms.get(0).getM_bm());
                                                        else imageView.setVisibility(View.GONE);
                                                    }
                                                    else if (showNum < pois1.get(0).getPhotonum() - 1) imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    else imageView.setVisibility(View.GONE);
                                                    Toast.makeText(singlepoi.this, "已经删除图片", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        dialog.show();
                                        ges = true;
                                    }
                                }
                                else if (event.getPointerCount() == 5) {
                                    Log.w(TAG, "5指滑动");
                                }
                                else if (event.getPointerCount() == 2) {
                                    Log.w(TAG, "2指滑动");
                                }
                                break;

                        }
                        return true;
                    }
                });
            }
        }
        else poi1.setToDefault("photonum");
        poi1.updateAll("poic = ?", POIC);
        Log.w(TAG, "refresh: " + poi1.updateAll("poic = ?", POIC));
        /*POI poi = new POI();
        poi.setPhotonum(photos.size());
        poi.setTapenum(tapes.size());
        poi.updateAll("POIC = ?", POIC);*/
        List<POI> pois1 = LitePal.where("poic = ?", POIC).find(POI.class);
        Log.w(TAG, "tapes2: " + pois1.get(0).getTapenum() + "\n");
        Log.w(TAG, "photos2: " + pois1.get(0).getPhotonum() + "\n");
        POI poi = pois.get(0);
        name = poi.getName();
        CacheName = name;
        editText_name = (EditText) findViewById(R.id.edit_name);
        editText_name.setVisibility(View.VISIBLE);
        editText_name.setText(name);

        editText_des = (EditText) findViewById(R.id.edit_des);
        editText_des.setVisibility(View.VISIBLE);
        if (poi.getDescription() != null) {
            editText_des.setText(poi.getDescription());
            CacheDescription = poi.getDescription();
        }else {
            editText_des.setText("");
            CacheDescription = "";
        }
        TextView textView_time = (TextView) findViewById(R.id.txt_timeshow);
        textView_time.setVisibility(View.VISIBLE);
        textView_time.setText(poi.getTime());
        Log.w(TAG, Integer.toString(tapes.size()));
        textView_photonum.setText(Integer.toString(photos.size()));
        textView_photonum.setVisibility(View.VISIBLE);
        textView_photonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                //打开图片列表
                Intent intent1 = new Intent(singlepoi.this, photoshow.class);
                intent1.putExtra("POIC", POIC);
                intent1.putExtra("type", 0);
                startActivity(intent1);
            }
        });
        textView_tapenum = (TextView) findViewById(R.id.txt_tapenumshow);
        textView_tapenum.setVisibility(View.VISIBLE);
        textView_tapenum.setText(Integer.toString(tapes.size()));
        textView_tapenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                //打开录音列表
                Intent intent2 = new Intent(singlepoi.this, tapeshow.class);
                intent2.putExtra("POIC", POIC);
                intent2.putExtra("type", 0);
                startActivity(intent2);
            }
        });


        TextView txt_videonum = (TextView) findViewById(R.id.txt_videonumshow);
        txt_videonum.setText(Integer.toString(videos.size()));
        txt_videonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                //打开视频列表
                Intent intent1 = new Intent(singlepoi.this, VideoShow.class);
                intent1.putExtra("POIC", POIC);
                intent1.putExtra("type", 0);
                startActivity(intent1);
            }
        });
        txt_videonum.setVisibility(View.VISIBLE);

        TextView textView_loc = (TextView) findViewById(R.id.txt_locshow);
        textView_loc.setVisibility(View.VISIBLE);
        DecimalFormat df = new DecimalFormat("0.0000");
        m_lat = poi.getX();
        m_lng = poi.getY();
        textView_loc.setText(df.format(poi.getX()) + ", " + df.format(poi.getY()));
        ImageButton addphoto = (ImageButton)findViewById(R.id.addPhoto_singlepoi);
        addphoto.setVisibility(View.VISIBLE);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                showPopueWindowForPhoto();
            }
        });
        ImageButton addtape = (ImageButton)findViewById(R.id.addTape_singlepoi);
        addtape.setVisibility(View.VISIBLE);
        addtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getSharedPreferences("sp_name_audio", MODE_PRIVATE)
                            .edit().clear()
                            .apply();

                    final RecordAudioDialogFragment fragment = RecordAudioDialogFragment.newInstance();
                    fragment.show(getSupportFragmentManager(),RecordAudioDialogFragment.class.getSimpleName());


                    fragment.setOnCancelListener(new RecordAudioDialogFragment.OnAudioCancelListener() {
                        @Override
                        public void onCancel() {
                            fragment.dismiss();

                            SharedPreferences prefTape = getSharedPreferences("sp_name_audio",MODE_PRIVATE);
                            String TapePath = prefTape.getString("audio_path","");

                            Log.w("audio_path_singlepo: ",TapePath);
                            Uri uri = Uri.parse(TapePath);
                            List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                            POI poi = new POI();
                            poi.setTapenum(POIs.get(0).getTapenum() + 1);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                            Date date = new Date(System.currentTimeMillis());
                            poi.updateAll("poic = ?", POIC);
                            MTAPE mtape = new MTAPE();
                            mtape.setPath(TapePath);
                            mtape.setPdfic(POIs.get(0).getIc());
                            mtape.setPoic(POIC);
                            mtape.setTime(simpleDateFormat.format(date));
                            mtape.save();
                        }
                    });

//                    CacheDescription = editText_des.getText().toString();
//                    CacheName = editText_name.getText().toString();
//                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }

            }
        });
        ImageButton AddVideo = (ImageButton)findViewById(R.id.addVideo_singlepoi);
        AddVideo.setVisibility(View.VISIBLE);
        AddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                showPopueWindowForVideo();
            }
        });
        FloatingActionButton fab_saveinfo = (FloatingActionButton) findViewById(R.id.fab_saveinfo1);
        fab_saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        POI poi = new POI();
                        poi.setName(editText_name.getText().toString());
                        name = editText_name.getText().toString();
                        poi.setDescription(editText_des.getText().toString());
                        poi.setType(str);
                        poi.updateAll("poic = ?", POIC);
                        Toast.makeText(singlepoi.this, R.string.SaveInfo, Toast.LENGTH_SHORT).show();
            }
        });
    }
public static  final int UPDATA_TEXT = 1;
    private TextView textViewShowNum;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATA_TEXT:
                    //在这里进行UI操作
                    List<MTAPE> tapes = LitePal.where("poic = ?", POIC).find(MTAPE.class);
                    Log.w("handler:py",Integer.toString(tapes.size()));
                    textViewShowNum.setText(Integer.toString(tapes.size()));break;
                    default:break;
            }
        }
    };
    private void refresh(){
        TextView txt_name = (TextView) findViewById(R.id.txt_name);
        txt_name.setVisibility(View.VISIBLE);
        TextView txt_time = (TextView) findViewById(R.id.txt_time);
        txt_time.setVisibility(View.VISIBLE);
        TextView txt_des = (TextView) findViewById(R.id.txt_des);
        txt_des.setVisibility(View.VISIBLE);
        TextView txt_type = (TextView) findViewById(R.id.txt_type);
        txt_type.setVisibility(View.VISIBLE);
        TextView txt_photonum = (TextView) findViewById(R.id.txt_photonum);
        txt_photonum.setVisibility(View.VISIBLE);
        TextView txt_tapenum = (TextView) findViewById(R.id.txt_tapenum);
        txt_tapenum.setVisibility(View.VISIBLE);
        TextView txt_loc = (TextView) findViewById(R.id.txt_loc);
        txt_loc.setVisibility(View.VISIBLE);

        List<POI> pois = LitePal.where("poic = ?", POIC).find(POI.class);
        List<MTAPE> tapes = LitePal.where("poic = ?", POIC).find(MTAPE.class);
        List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
        final List<MPHOTO> photos = LitePal.where("poic = ?", POIC).find(MPHOTO.class);

        //
        String[] strings = getResources().getStringArray(R.array.Type);
        for (int i = 0; i < strings.length; i++) {
            Log.w(TAG, "refresh: " + strings[i]);
            if (strings[i].equals(pois.get(0).getType())) type_spinner.setSelection(i);
        }
        //

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = type_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                str = type_spinner.getSelectedItem().toString();
            }
        });
        getBitmap(photos);
        Log.w(TAG, "pois: " + pois.size() + "\n");
        Log.w(TAG, "tapes1: " + pois.get(0).getTapenum() + "\n");
        Log.w(TAG, "photos1: " + pois.get(0).getPhotonum() + "\n");
        Log.w(TAG, "tapes: " + tapes.size() + "\n");
        Log.w(TAG, "photos: " + photos.size());
        POI poi1 = new POI();
        if (tapes.size() != 0) poi1.setTapenum(tapes.size());
        else poi1.setToDefault("tapenum");
        if (photos.size() != 0) {
            poi1.setPhotonum(photos.size());
            final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
            /*imageView.setVisibility(View.VISIBLE);
            String path = photos.get(0).getPath();
            File file = new File(path);
            try {
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    int degree = DataUtil.getPicRotate(path);
                    if (degree != 0) {
                        Matrix m = new Matrix();
                        m.setRotate(degree); // 旋转angle度
                        Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    }
                    imageView.setImageBitmap(bitmap);
                }else {
                    Drawable drawable = MyApplication.getContext().getResources().getDrawable(R.drawable.imgerror);
                    BitmapDrawable bd = (BitmapDrawable) drawable;
                    Bitmap bitmap = Bitmap.createBitmap(bd.getBitmap(), 0, 0, bd.getBitmap().getWidth(), bd.getBitmap().getHeight());
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, 80, 120,
                            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    imageView.setImageBitmap(bitmap);
                }
            }catch (IOException e){
                Log.w(TAG, e.toString());
            }*/
            if (photos.size() >= 1) {
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        float distanceX = 0;
                        float distanceY = 0;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //第一个手指按下
                                pt0.set(event.getX(0), event.getY(0));
                                Log.w(TAG, "onTouchdown: " + event.getX());
                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                Log.w(TAG, "ACTION_POINTER_DOWN");
                                Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                //第二个手指按下
                                Log.w(TAG, "手指id: " + event.getActionIndex());
                                Log.w(TAG, "onTouchdown: " + event.getX());
                                Log.w(TAG, "ACTION_POINTER_DOWN");
                                Log.w(TAG, "同时按下的手指数量: " + event.getPointerCount());
                                break;
                            case MotionEvent.ACTION_UP:
                                //最后一个手指抬起
                                ges = false;
                                    Log.w(TAG, "onTouchup: " + event.getX());
                                    Log.w(TAG, "getPointerId: " + event.getPointerId(0));
                                    distanceX = event.getX(0) - pt0.x;
                                    distanceY = event.getY(0) - pt0.y;
                                    Log.w(TAG, "onTouch: " + distanceX);
                                    if (Math.abs(distanceX) > Math.abs(distanceY) & Math.abs(distanceX) > 200 & Math.abs(distanceY) < 100) {
                                        if (distanceX > 0) {
                                            Log.w(TAG, "bms.size : " + bms.size());
                                            showNum++;
                                            if (showNum > bms.size() - 1) {
                                                showNum = 0;
                                                imageView.setImageBitmap(bms.get(0).getM_bm());
                                            } else {
                                                imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                            }
                                        } else {
                                            showNum--;
                                            if (showNum < 0) {
                                                showNum = bms.size() - 1;
                                                imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                            } else {
                                                imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                            }
                                        }
                                    Log.w(TAG, "同时抬起的手指数量: " + event.getPointerCount());
                                    Log.w(TAG, "手指id: " + event.getActionIndex());
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (event.getPointerCount() == 3) {
                                    Log.w(TAG, "3指滑动");

                                }
                                else if (event.getPointerCount() == 4) {
                                    if (!ges) {
                                        Log.w(TAG, "4指滑动");
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
                                        dialog.setTitle("提示");
                                        dialog.setMessage("确认删除图片吗?");
                                        dialog.setCancelable(false);
                                        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                List<POI> pois1 = LitePal.where("poic = ?", POIC).find(POI.class);
                                                if (pois1.get(0).getPhotonum() > 0) {
                                                    textView_photonum.setText(Integer.toString(pois1.get(0).getPhotonum() - 1));
                                                    POI poi = new POI();
                                                    poi.setPhotonum(pois1.get(0).getPhotonum() - 1);
                                                    poi.updateAll("poic = ?", POIC);
                                                    LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, bms.get(showNum).getM_path());
                                                    bms.remove(showNum);
                                                    if (showNum > pois1.get(0).getPhotonum() - 1) {
                                                        if (bms.size() > 0) imageView.setImageBitmap(bms.get(0).getM_bm());
                                                        else imageView.setVisibility(View.GONE);
                                                    }
                                                    else if (showNum < pois1.get(0).getPhotonum() - 1) imageView.setImageBitmap(bms.get(showNum).getM_bm());
                                                    else imageView.setVisibility(View.GONE);
                                                    Toast.makeText(singlepoi.this, "已经删除图片", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        dialog.show();
                                        ges = true;
                                    }
                                }
                                else if (event.getPointerCount() == 5) {
                                    Log.w(TAG, "5指滑动");
                                }
                                else if (event.getPointerCount() == 2) {
                                    Log.w(TAG, "2指滑动");
                                }
                                break;

                        }
                        return true;
                    }
                });
            }
        }
        else poi1.setToDefault("photonum");
        poi1.updateAll("poic = ?", POIC);
        Log.w(TAG, "refresh: " + poi1.updateAll("poic = ?", POIC));
        /*POI poi = new POI();
        poi.setPhotonum(photos.size());
        poi.setTapenum(tapes.size());
        poi.updateAll("POIC = ?", POIC);*/
        List<POI> pois1 = LitePal.where("poic = ?", POIC).find(POI.class);
        Log.w(TAG, "tapes2: " + pois1.get(0).getTapenum() + "\n");
        Log.w(TAG, "photos2: " + pois1.get(0).getPhotonum() + "\n");
        POI poi = pois.get(0);
        name = poi.getName();
        editText_name = (EditText) findViewById(R.id.edit_name);
        editText_name.setVisibility(View.VISIBLE);
        // TODO 2021/1/27 数据缓存
        if (!CacheName.equals(name))
            editText_name.setText(CacheName);

        editText_des = (EditText) findViewById(R.id.edit_des);
        editText_des.setVisibility(View.VISIBLE);
        if (poi.getDescription() != null) {
            editText_des.setText(poi.getDescription());
        }else editText_des.setText("");
        if (!CacheDescription.equals(editText_des.getText().toString()))
            editText_des.setText(CacheDescription);

        TextView textView_time = (TextView) findViewById(R.id.txt_timeshow);
        textView_time.setVisibility(View.VISIBLE);
        textView_time.setText(poi.getTime());
        Log.w(TAG, Integer.toString(tapes.size()));
        textView_tapenum.setText(Integer.toString(tapes.size()));
        textView_photonum.setText(Integer.toString(photos.size()));
        textView_photonum.setVisibility(View.VISIBLE);
        textView_photonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                //打开图片列表
                Intent intent1 = new Intent(singlepoi.this, photoshow.class);
                intent1.putExtra("POIC", POIC);
                intent1.putExtra("type", 0);
                startActivity(intent1);
            }
        });
        //textView_tapenum.setVisibility(View.VISIBLE);
        //textViewShowNum.setText(Integer.toString(tapes.size()));
        textViewShowNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                //打开录音列表
                Intent intent2 = new Intent(singlepoi.this, tapeshow.class);
                intent2.putExtra("POIC", POIC);
                intent2.putExtra("type", 0);
                startActivity(intent2);
            }
        });


        TextView txt_videonum = (TextView) findViewById(R.id.txt_videonumshow);
        txt_videonum.setText(Integer.toString(videos.size()));
        txt_videonum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                //打开图片列表
                Intent intent1 = new Intent(singlepoi.this, VideoShow.class);
                intent1.putExtra("POIC", POIC);
                intent1.putExtra("type", 0);
                startActivity(intent1);
            }
        });
        txt_videonum.setVisibility(View.VISIBLE);

        TextView textView_loc = (TextView) findViewById(R.id.txt_locshow);
        textView_loc.setVisibility(View.VISIBLE);
        DecimalFormat df = new DecimalFormat("0.0000");
        m_lat = poi.getX();
        m_lng = poi.getY();
        textView_loc.setText(df.format(poi.getX()) + ", " + df.format(poi.getY()));
        ImageButton addphoto = (ImageButton)findViewById(R.id.addPhoto_singlepoi);
        addphoto.setVisibility(View.VISIBLE);
        addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                showPopueWindowForPhoto();
            }
        });
        ImageButton addtape = (ImageButton)findViewById(R.id.addTape_singlepoi);
        addtape.setVisibility(View.VISIBLE);
        addtape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getSharedPreferences("sp_name_audio", MODE_PRIVATE)
                            .edit().clear()
                            .apply();

                    final RecordAudioDialogFragment fragment = RecordAudioDialogFragment.newInstance();
                    fragment.show(getSupportFragmentManager(),RecordAudioDialogFragment.class.getSimpleName());
                    fragment.setCancelable(false);

                    fragment.setOnCancelListener(new RecordAudioDialogFragment.OnAudioCancelListener() {
                        @Override
                        public void onCancel() {
                            fragment.dismiss();
                            SharedPreferences prefTape = getSharedPreferences("sp_name_audio", MODE_PRIVATE);
                            String TapePath = prefTape.getString("audio_path", "");
                            Log.w("audio_path_singlepoii+", "py:" + TapePath);

                            if (!TapePath.equals("")){
                                Uri uri = Uri.parse(TapePath);
                                List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                                POI poi = new POI();
                                poi.setTapenum(POIs.get(0).getTapenum() + 1);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                                Date date = new Date(System.currentTimeMillis());
                                poi.updateAll("poic = ?", POIC);
                                MTAPE mtape = new MTAPE();
                                mtape.setPath(TapePath);
                                mtape.setPdfic(POIs.get(0).getIc());
                                mtape.setPoic(POIC);
                                mtape.setTime(simpleDateFormat.format(date));
                                mtape.save();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = UPDATA_TEXT;
                                        handler.sendMessage(message);
                                    }
                                }).start();
                        }
                        }
                    });

//                    CacheDescription = editText_des.getText().toString();
//                    CacheName = editText_name.getText().toString();
//                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageButton AddVideo = (ImageButton)findViewById(R.id.addVideo_singlepoi);
        AddVideo.setVisibility(View.VISIBLE);
        AddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheDescription = editText_des.getText().toString();
                CacheName = editText_name.getText().toString();
                showPopueWindowForVideo();
            }
        });
        FloatingActionButton fab_saveinfo = (FloatingActionButton) findViewById(R.id.fab_saveinfo1);
        fab_saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POI poi = new POI();
                poi.setName(editText_name.getText().toString());
                name = editText_name.getText().toString();
                poi.setDescription(editText_des.getText().toString());
                poi.setType(str);
                poi.updateAll("poic = ?", POIC);
                Toast.makeText(singlepoi.this, R.string.SaveInfo, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String CacheDescription = "";
    private String CacheName = "";

    private void getBitmap(final List<MPHOTO> photos){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.w(TAG, "run: photo.size" + photos.size());
                bms = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++) {
                    String path = photos.get(i).getPath();
                    File file = new File(path);
                    if (file.exists()) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                            int degree = DataUtil.getPicRotate(path);
                            if (degree != 0) {
                                Matrix m = new Matrix();
                                m.setRotate(degree); // 旋转angle度
                                Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                            }
                            bms.add(new bt(bitmap,  path));
                        } catch (IOException e) {
                            Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                            bms.add(new bt(bitmap, ""));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(bms.get(0).getM_bm());
                                }
                            });
                        }
                    } else {
                        Bitmap bitmap = Bitmap.createBitmap(80, 120, Bitmap.Config.ALPHA_8);
                        bms.add(new bt(bitmap, ""));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ImageView imageView = (ImageView) findViewById(R.id.photo_image_singlepoi);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(bms.get(0).getM_bm());
                        }
                    });
                }

                Log.w(TAG, "getBitmap: " + bms.size());
            }
        }).start();
    }

    private void showPopueWindowForPhoto(){
        View popView = View.inflate(this,R.layout.popupwindow_camera_need,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1/3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight ,height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPicker();
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                popupWindow.dismiss();

            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    private void takePhoto(){
        File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/photo");
        if (!file2.exists() && !file2.isDirectory()){
            file2.mkdirs();
        }
        long timenow = System.currentTimeMillis();
        File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/photo", Long.toString(timenow) + ".jpg");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            //locError(Environment.getExternalStorageDirectory() + "/maphoto/" + Long.toString(timenow) + ".jpg");
            imageUri = FileProvider.getUriForFile(singlepoi.this, "com.android.tuzhi.fileprovider", outputImage);

        }else imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void showPopueWindowForVideo(){
        View popView = View.inflate(this,R.layout.popupwindow_addvideo,null);
        Button bt_pickvideo = (Button) popView.findViewById(R.id.btn_pop_pickvideo);
        Button bt_takevideo = (Button) popView.findViewById(R.id.btn_pop_takevideo);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel_video);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1/3;

        final PopupWindow popupWindow = new PopupWindow(popView, weight ,height);
        //popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_pickvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launchPicker();
                pickVideo(singlepoi.this);
                popupWindow.dismiss();

            }
        });
        bt_takevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //takePhoto();
                takeVideo(singlepoi.this);
                popupWindow.dismiss();

            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    public void pickVideo(Activity activity){

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 119);
        }else {
            pickVideo();
        }
    }

    private void pickVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent,
                FLAG_REQUEST_SYSTEM_VIDEO);
    }

    public void takeVideo(Activity activity){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 118);
        }else {
            takeVideo();
        }
    }

    private void takeVideo(){
        File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video");
        if (!file2.exists() && !file2.isDirectory()){
            file2.mkdirs();
        }
        long timenow = System.currentTimeMillis();
        File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video", Long.toString(timenow) + ".mp4");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            //locError(Environment.getExternalStorageDirectory() + "/maphoto/" + Long.toString(timenow) + ".jpg");
            imageUri = FileProvider.getUriForFile(this, "com.android.tuzhi.fileprovider", outputImage);

        }else imageUri = Uri.fromFile(outputImage);
        Log.w(TAG, "takeVideo: " + imageUri.toString());
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, FLAG_REQUEST_CAMERA_VIDEO);
    }

    public void ResultForPickVideo(Uri uri){

        try {
            String path = DataUtil.getRealPathFromUriForVedio(this, uri);
            /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            int degree = DataUtil.getPicRotate(path);
            if (degree != 0) {
                Matrix m = new Matrix();
                m.setRotate(degree); // 旋转angle度
                Log.w(TAG, "showPopueWindowForPhoto: " + degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }*/
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
            Date date = new Date(System.currentTimeMillis());
            List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
            POI poi = new POI();
            long time = System.currentTimeMillis();
            poi.setPhotonum(POIs.get(0).getVedionum() + 1);
            poi.updateAll("poic = ?", POIC);
            MVEDIO mvedio = new MVEDIO();
            mvedio.setPdfic(POIs.get(0).getIc());//@@@@漏掉的一行
            mvedio.setPoic(POIC);
            mvedio.setPath(path);
            mvedio.setTime(simpleDateFormat.format(date));
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            int degree = DataUtil.getPicRotate(path);
            if (degree != 0) {
                Matrix m = new Matrix();
                m.setRotate(degree); // 旋转angle度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            }
            File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img");
            if (!file2.exists() && !file2.isDirectory()){
                file2.mkdirs();
            }
            long timenow = System.currentTimeMillis();
            File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img", Long.toString(timenow) + ".jpg");
            try {
                if (outputImage.exists()){
                    outputImage.delete();
                }
                outputImage.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            DataUtil.saveBitmap(bitmap, outputImage.getAbsolutePath());
            mvedio.setThumbnailImg(outputImage.getAbsolutePath());
            mvedio.save();
            List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
            TextView txt_videonum = (TextView) findViewById(R.id.txt_videonumshow);
            txt_videonum.setText(Integer.toString(videos.size()));
            /*iv.setImageBitmap(bitmap);
            videoView.setVideoPath(path);//setVideoURI(Uri.parse(uriString));
            videoView.start();*/
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
        }
        catch (Exception e){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 118:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, singlepoi.this.getResources().getText(R.string.PermissionError), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }else {
                            takeVideo();
                        }
                    }
                }
                break;
            case 119:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, singlepoi.this.getResources().getText(R.string.PermissionError), Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }else {
                            pickVideo();
                        }
                    }
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_CODE_PHOTO:
                    Uri uri = data.getData();
                    if (POITYPE == 0) {
                        List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                        POI poi = new POI();
                        poi.setPhotonum(POIs.get(0).getPhotonum() + 1);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                        Date date = new Date(System.currentTimeMillis());
                        poi.updateAll("poic = ?", POIC);
                        MPHOTO mphoto = new MPHOTO();
                        mphoto.setPdfic(POIs.get(0).getIc());
                        mphoto.setPoic(POIC);
                        //mphoto.setPath(getRealPath(uri.getPath()));
                        mphoto.setPath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        mphoto.setTime(simpleDateFormat.format(date));
                        mphoto.save();
                    } else if (POITYPE == 1) {
                        List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                        DMBZ dmbz = new DMBZ();
                        if (dmbzs.get(0).getIMGPATH() != null) {
                            String imgpath = dmbzs.get(0).getIMGPATH();
                            if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                dmbz.setIMGPATH(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                            else dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        } else dmbz.setIMGPATH(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        dmbz.updateAll("xh = ?", DMXH);
                    } else if (POITYPE == 2) {
                        List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                        DMLine dmLine = new DMLine();
                        if (dmLines.get(0).getImgpath() != null) {
                            String imgpath = dmLines.get(0).getImgpath();
                            if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                dmLine.setImgpath(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                            else dmLine.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        } else dmLine.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        dmLine.updateAll("mapid = ?", DML);
                    } else if (POITYPE == 3) {
                        List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                        DMPoint dmPoint = new DMPoint();
                        if (dmPoints.get(0).getImgpath() != null) {
                            String imgpath = dmPoints.get(0).getImgpath();
                            if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                dmPoint.setImgpath(imgpath + "|" + DataUtil.getRealPathFromUriForPhoto(this, uri));
                            else dmPoint.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        } else dmPoint.setImgpath(DataUtil.getRealPathFromUriForPhoto(this, uri));
                        dmPoint.updateAll("mapid = ?", DMP);
                    }
                    break;
                case REQUEST_CODE_TAPE:
                    uri = data.getData();
                    if (DataUtil.getRealPathFromUriForAudio(this, uri).contains("Android/data")){
                        Toast.makeText(singlepoi.this, "权限问题，该手机无法正常保存录音文件！", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (POITYPE == 0) {
                            List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                            POI poi = new POI();
                            poi.setTapenum(POIs.get(0).getTapenum() + 1);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                            Date date = new Date(System.currentTimeMillis());
                            poi.updateAll("poic = ?", POIC);
                            MTAPE mtape = new MTAPE();
                            mtape.setPath(DataUtil.getRealPathFromUriForAudio(this, uri));
                            mtape.setPdfic(POIs.get(0).getIc());
                            mtape.setPoic(POIC);
                            mtape.setTime(simpleDateFormat.format(date));
                            mtape.save();
                        }
                    }
                    break;
                case TAKE_PHOTO:
                    //Log.w(TAG, "onActivityResult1: " + imageUri.toString());
                    if (imageUri != null) {
                        String imageuri;
                        if (Build.VERSION.SDK_INT >= 24) {
                            imageuri = DataUtil.getRealPath(imageUri.toString());
                        } else {
                            imageuri = imageUri.toString().substring(7);
                        }
                        File file = new File(imageuri);
                        Log.w(TAG, "onActivityResult2: " + imageuri);
                        if (file.length() != 0) {
                            try {
                                MediaStore.Images.Media.insertImage(getContentResolver(), imageuri, "title", "description");
                                // 最后通知图库更新
                                singlepoi.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageuri)));
                            } catch (IOException e) {
                            }
                            if (POITYPE == 0) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                                Date date = new Date(System.currentTimeMillis());
                                List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                                POI poi = new POI();
                                long time = System.currentTimeMillis();
                                poi.setPhotonum(POIs.get(0).getPhotonum() + 1);
                                poi.updateAll("poic = ?", POIC);
                                MPHOTO mphoto = new MPHOTO();
                                mphoto.setPdfic(POIs.get(0).getIc());//.....
                                mphoto.setPoic(POIC);
                                mphoto.setPath(imageuri);
                                mphoto.setTime(simpleDateFormat.format(date));
                                mphoto.save();
                            } else if (POITYPE == 1) {
                                List<DMBZ> dmbzs = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
                                DMBZ dmbz = new DMBZ();
                                if (dmbzs.get(0).getIMGPATH() != null) {
                                    String imgpath = dmbzs.get(0).getIMGPATH();
                                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                        dmbz.setIMGPATH(imgpath + "|" + imageuri);
                                    else dmbz.setIMGPATH(imageuri);
                                } else dmbz.setIMGPATH(imageuri);
                                dmbz.updateAll("xh = ?", DMXH);
                            } else if (POITYPE == 2) {
                                List<DMLine> dmLines = LitePal.where("mapid = ?", DML).find(DMLine.class);
                                DMLine dmLine = new DMLine();
                                if (dmLines.get(0).getImgpath() != null) {
                                    String imgpath = dmLines.get(0).getImgpath();
                                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                        dmLine.setImgpath(imgpath + "|" + imageuri);
                                    else dmLine.setImgpath(imageuri);
                                } else dmLine.setImgpath(imageuri);
                                dmLine.updateAll("mapid = ?", DML);
                            } else if (POITYPE == 3) {
                                List<DMPoint> dmPoints = LitePal.where("mapid = ?", DMP).find(DMPoint.class);
                                DMPoint dmPoint = new DMPoint();
                                if (dmPoints.get(0).getImgpath() != null) {
                                    String imgpath = dmPoints.get(0).getImgpath();
                                    if (DataUtil.appearNumber(imgpath, "\\|") + 1 > 0)
                                        dmPoint.setImgpath(imgpath + "|" + imageuri);
                                    else dmPoint.setImgpath(imageuri);
                                } else dmPoint.setImgpath(imageuri);
                                dmPoint.updateAll("mapid = ?", DMP);
                            }
                        } else {
                            file.delete();
                            Toast.makeText(singlepoi.this, R.string.TakePhotoError, Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case FLAG_REQUEST_SYSTEM_VIDEO:
                    uri = data.getData();
                    ResultForPickVideo(uri);
                    break;
                case FLAG_REQUEST_CAMERA_VIDEO:
                    if (imageUri!=null) {
                        Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();
                        String imageuri1;
                        if (Build.VERSION.SDK_INT >= 24) {
                            imageuri1 = DataUtil.getRealPath(imageUri.toString());
                        } else {
                            imageuri1 = imageUri.toString().substring(7);
                        }
                    /*videoView.setVideoPath(imageuri);//setVideoURI(Uri.parse(uriString));
                    videoView.start();*/
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(singlepoi.this.getResources().getText(R.string.DateAndTime).toString());
                            Date date = new Date(System.currentTimeMillis());
                            List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                            POI poi = new POI();
                            poi.setPhotonum(POIs.get(0).getVedionum() + 1);
                            poi.updateAll("poic = ?", POIC);
                            MVEDIO mvedio = new MVEDIO();
                            mvedio.setPdfic(POIs.get(0).getIc());//@@@@漏掉的一行
                            mvedio.setPoic(POIC);
                            mvedio.setPath(imageuri1);
                            mvedio.setTime(simpleDateFormat.format(date));
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(imageuri1);
                            Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                            int degree = DataUtil.getPicRotate(imageuri1);
                            if (degree != 0) {
                                Matrix m = new Matrix();
                                m.setRotate(degree); // 旋转angle度
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                            }
                            File file2 = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img");
                            if (!file2.exists() && !file2.isDirectory()) {
                                file2.mkdirs();
                            }
                            long timenow = System.currentTimeMillis();
                            File outputImage = new File(Environment.getExternalStorageDirectory() + "/TuZhi/video/img", Long.toString(timenow) + ".jpg");
                            try {
                                if (outputImage.exists()) {
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            DataUtil.saveBitmap(bitmap, outputImage.getAbsolutePath());
                            mvedio.setThumbnailImg(outputImage.getAbsolutePath());
                            mvedio.save();
                            List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
                            TextView txt_videonum = (TextView) findViewById(R.id.txt_videonumshow);
                            txt_videonum.setText(Integer.toString(videos.size()));
                            //iv.setImageBitmap(bitmap);
                        } catch (Exception e) {

                        }
                    }
                    break;
            }
        }
    }

    void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, REQUEST_CODE_PHOTO);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poiinfotoolbar, menu);
        menu.findItem(R.id.back_pois).setVisible(false);
        menu.findItem(R.id.restore_pois).setVisible(false);
        menu.findItem(R.id.add_pois).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.query_poi_map:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(singlepoi.this);
                dialog1.setTitle("提示");
                dialog1.setMessage("请选择定位方式");
                dialog1.setCancelable(false);
                dialog1.setPositiveButton("图上位置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (POITYPE == 0) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", POIC);
                            editor.apply();
                        }else if (POITYPE == 1) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", "DMBZ" + DMXH);
                            editor.apply();
                        }else if (POITYPE == 2) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", "DML" + DML);
                            editor.apply();
                        }else if (POITYPE == 3) {
                            SharedPreferences.Editor editor = getSharedPreferences("update_query_attr_to_map", MODE_PRIVATE).edit();
                            editor.putString("poic", "DMP" + DMP);
                            editor.apply();
                        }
                        singlepoi.this.finish();
                    }
                });
                dialog1.setNegativeButton("路径规划", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Point pt = new Point(LatLng.wgs84togcj02(m_lng, m_lat));
                        Log.w(TAG, "onClick: " + m_lng + ", " + m_lat);
                        try {
                            if (CheckApkExist.checkTencentMapExist(MyApplication.getContext())) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("qqmap://map/routeplan?type=walk&to=" + name + "&tocoord=" + pt.getY() + "," + pt.getX() + "&policy=1&referer=图志"));
                                startActivity(intent);
                            }else if (CheckApkExist.checkGaodeMapExist(MyApplication.getContext())) {
                                Intent intent = new Intent();
                                intent.setPackage("com.autonavi.minimap");
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse("androidamap://route?sourceApplication=图志&dlat=" + pt.getY() + "&dlon=" + pt.getX() + "&dname=" + name + "&dev=1&t=2"));
                                startActivity(intent);
                            }else if (CheckApkExist.checkBaiduMapExist(MyApplication.getContext())) {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("baidumap://map/direction?" +
                                        "destination=latlng:" + m_lat + "," + m_lng + "|name:" + name +"&mode=walking"));
                                startActivity(intent);
                            }else {
                                Toast.makeText(singlepoi.this, R.string.QueryPathError, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(singlepoi.this, R.string.QueryPathError1, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog1.show();
                break;
            case R.id.back_andupdate:
                if (POITYPE == 0) {
                    POI poi = new POI();
                    poi.setName(editText_name.getText().toString());
                    poi.setDescription(editText_des.getText().toString());
                    poi.setType(str);
                    poi.updateAll("poic = ?", POIC);
                }else if (POITYPE == 1) {
                    DMBZ poi = new DMBZ();
                    poi.setXH(DMXH);
                    poi.setDY(edit_DY.getText().toString());
                    poi.setMC(edit_MC.getText().toString());
                    poi.setBZMC(edit_BZMC.getText().toString());
                    poi.setXZQMC(edit_XZQMC.getText().toString());
                    poi.setXZQDM(edit_XZQDM.getText().toString());
                    poi.setSZDW(edit_SZDW.getText().toString());
                    poi.setSCCJ(edit_SCCJ.getText().toString());
                    poi.setGG(edit_GG.getText().toString());
                    poi.updateAll("xh = ?", DMXH);
                }else if (POITYPE == 2) {
                    DMLine dmLine = new DMLine();
                    dmLine.setQydm(edit_qydm.getText().toString());
                    dmLine.setLbdm(edit_lbdm.getText().toString());
                    dmLine.setBzmc(edit_bzmc.getText().toString());
                    dmLine.setCym(edit_cym.getText().toString());
                    dmLine.setJc(edit_jc.getText().toString());
                    dmLine.setBm(edit_bm.getText().toString());
                    dmLine.setDfyz(edit_dfyz.getText().toString());
                    dmLine.setZt(edit_zt.getText().toString());
                    dmLine.setDmll(edit_dmll.getText().toString());
                    dmLine.setDmhy(edit_dmhy.getText().toString());
                    dmLine.setLsyg(edit_lsyg.getText().toString());
                    dmLine.setDlstms(edit_dlstms.getText().toString());
                    dmLine.setZlly(edit_zlly.getText().toString());
                    dmLine.updateAll("mapid = ?", DML);
                }else if (POITYPE == 3) {
                    DMPoint dmPoint = new DMPoint();
                    dmPoint.setQydm(edit_qydm.getText().toString());
                    dmPoint.setLbdm(edit_lbdm.getText().toString());
                    dmPoint.setBzmc(edit_bzmc.getText().toString());
                    dmPoint.setCym(edit_cym.getText().toString());
                    dmPoint.setJc(edit_jc.getText().toString());
                    dmPoint.setBm(edit_bm.getText().toString());
                    dmPoint.setDfyz(edit_dfyz.getText().toString());
                    dmPoint.setZt(edit_zt.getText().toString());
                    dmPoint.setDmll(edit_dmll.getText().toString());
                    dmPoint.setDmhy(edit_dmhy.getText().toString());
                    dmPoint.setLsyg(edit_lsyg.getText().toString());
                    dmPoint.setDlstms(edit_dlstms.getText().toString());
                    dmPoint.setZlly(edit_zlly.getText().toString());
                    dmPoint.updateAll("mapid = ?", DMP);
                }
                this.finish();
                break;
            case R.id.deletepoi:
                AlertDialog.Builder dialog = new AlertDialog.Builder(singlepoi.this);
                dialog.setTitle("提示");
                dialog.setMessage("确认删除兴趣点吗?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (POITYPE == 0) {
                            LitePal.deleteAll(POI.class, "poic = ?", POIC);
                            LitePal.deleteAll(MPHOTO.class, "poic = ?", POIC);
                            LitePal.deleteAll(MTAPE.class, "poic = ?", POIC);
                            try {
                                List<MVEDIO> videos = LitePal.where("poic = ?", POIC).find(MVEDIO.class);
                                for (int j = 0; j < videos.size(); j++) {
                                    {
                                        File file = new File(videos.get(j).getThumbnailImg());
                                        file.delete();
                                        break;
                                    }
                                }
                            }
                            catch (Exception e){

                            }
                            LitePal.deleteAll(MVEDIO.class, "poic = ?", POIC);
                        }else if (POITYPE == 1) {
                            LitePal.deleteAll(DMBZ.class, "xh = ?", DMXH);
                        }else if (POITYPE == 2) {
                            LitePal.deleteAll(DMLine.class, "mapid = ?", DML);
                        }else if (POITYPE == 3) {
                            LitePal.deleteAll(DMPoint.class, "mapid = ?", DMP);
                        }
                        singlepoi.this.finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                break;
            default:
        }
        return true;
    }
}
