package com.geopdfviewer.android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 录音显示页面
 * 用于显示兴趣点中的录音内容
 *
 * @author  李正洋
 *
 * @since   1.7
 */
public class tapeshow extends AppCompatActivity {
    private static final String TAG = "tapeshow";
    private String POIC;
    private List<mTapeobj> mTapeobjList = new ArrayList<>();
    private mTapeobjAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private String deletePath;
    private final static int REQUEST_CODE_TAPE = 43;
    private int isLongClick = 1;
    Toolbar toolbar;
    private int POIType;
    private String DMXH;
    private String DML;
    private String DMP;

    private void refreshCardFromPOI(){
        mTapeobjList.clear();
        List<MTAPE> mtapes = LitePal.where("POIC = ?", POIC).find(MTAPE.class);
        for (MTAPE mtape : mtapes){
            mTapeobj mtapeobj = new mTapeobj(mtape.getPoic(), mtape.getPoic(), mtape.getTime(), mtape.getPath());
            mTapeobjList.add(mtapeobj);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tape);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mTapeobjAdapter(mTapeobjList);
        adapter.setOnItemLongClickListener(new mTapeobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path) {
                setTitle(tapeshow.this.getResources().getText(R.string.IsLongClicking));
                if (isLongClick == 0){
                    mTapeobjAdapter.ViewHolder holder = new mTapeobjAdapter.ViewHolder(view);
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            if (!deletePath.contains(replace)){
                                replace = path + "wslzy";
                            }
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    deletePath = path;
                    isLongClick = 0;
                    invalidateOptionsMenu();
                    Log.w(TAG, "onItemLongClick: " + deletePath );
                }
            }
        });
        adapter.setOnItemClickListener(new mTapeobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                mTapeobjAdapter.ViewHolder holder = new mTapeobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            if (!deletePath.contains(replace)){
                                replace = path + "wslzy";
                            }
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    File file = new File(path);
                    if (file.exists()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(tapeshow.this, Uri.parse(path));
                        mediaPlayer.start();
                    }else {
                        Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                    }
                }
                Log.w(TAG, "onItemClick: " + deletePath );
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void refreshCardFromDMBZ(){
        mTapeobjList.clear();
        List<DMBZ> dmbzList = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
        String ImgPathTemp = dmbzList.get(0).getTAPEPATH();
        if (ImgPathTemp != null) {
            String[] imgPath = new String[DataUtil.appearNumber(ImgPathTemp, ".mp3")];
            Log.w(TAG, "run: " + ImgPathTemp);
            for (int k = 0; k < imgPath.length; k++) {
                imgPath[k] = ImgPathTemp.substring(0, ImgPathTemp.indexOf(".mp3") + 4);
                if (k < imgPath.length - 1)
                    ImgPathTemp = ImgPathTemp.substring(ImgPathTemp.indexOf(".mp3") + 5);
                Log.w(TAG, "run: " + ImgPathTemp);
            }
            for (int kk = 0; kk < imgPath.length; kk++) {
                String rootpath = Environment.getExternalStorageDirectory().toString() + "/地名标志录音/";
                String path;
                if (!imgPath[kk].contains(Environment.getExternalStorageDirectory().toString())) {
                    path = rootpath + imgPath[kk];
                } else {
                    path = imgPath[kk];
                }
                mTapeobj mtapeobj = new mTapeobj(dmbzList.get(0).getBZMC(), dmbzList.get(0).getBZMC(), dmbzList.get(0).getTime(), path);
                mTapeobjList.add(mtapeobj);
            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_tape);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new mTapeobjAdapter(mTapeobjList);
        adapter.setOnItemLongClickListener(new mTapeobjAdapter.OnRecyclerItemLongListener() {
            @Override
            public void onItemLongClick(View view, String path) {
                setTitle(tapeshow.this.getResources().getText(R.string.IsLongClicking));
                deletePath = path;
                isLongClick = 0;
                invalidateOptionsMenu();
                Log.w(TAG, "onItemLongClick: " + deletePath );
            }
        });
        adapter.setOnItemClickListener(new mTapeobjAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, String path, int position) {
                mTapeobjAdapter.ViewHolder holder = new mTapeobjAdapter.ViewHolder(view);
                if (isLongClick == 0){
                    if (holder.cardView.getCardBackgroundColor().getDefaultColor() != Color.GRAY){
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                        deletePath = deletePath + "wslzy" + path;
                    }else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                        if (deletePath.contains("wslzy")) {
                            String replace = "wslzy" + path;
                            deletePath = deletePath.replace(replace, "");
                        }else {
                            resetView();
                        }
                    }
                }else {
                    File file = new File(path);
                    if (file.exists()) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(tapeshow.this, Uri.parse(path));
                        mediaPlayer.start();
                    }else {
                        Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError1, Toast.LENGTH_LONG).show();
                    }
                }
                Log.w(TAG, "onItemClick: " + deletePath );
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void resetView(){
        isLongClick = 1;
        setTitle(tapeshow.this.getResources().getText(R.string.TapeList));
        if (POIType == 0)
            refreshCardFromPOI();
        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapeshow);
        //声明ToolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        setTitle(tapeshow.this.getResources().getText(R.string.TapeList));
        Intent intent = getIntent();
        POIType = intent.getIntExtra("type", -1);
        if (POIType == 0) POIC = intent.getStringExtra("POIC");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (POIType == 0)
            refreshCardFromPOI();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (isLongClick){
            case 1:
                toolbar.setBackgroundColor(Color.rgb(63, 81, 181));
                menu.findItem(R.id.deletepoi).setVisible(false);
                menu.findItem(R.id.restore_pois).setVisible(false);
                break;
            case 0:
                toolbar.setBackgroundColor(Color.rgb(233, 150, 122));
                menu.findItem(R.id.back_pois).setVisible(false);
                menu.findItem(R.id.deletepoi).setVisible(true);
                menu.findItem(R.id.restore_pois).setVisible(true);
                menu.findItem(R.id.add_pois).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poiinfotoolbar, menu);
        menu.findItem(R.id.back_andupdate).setVisible(false);
        menu.findItem(R.id.query_poi_map).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.back_pois:
                this.finish();
                break;
            case R.id.restore_pois:
                resetView();
                break;
            case R.id.deletepoi:
                isLongClick = 1;
                invalidateOptionsMenu();
                //LitePal.deleteAll(MTAPE.class, "POIC = ?", POIC, "path = ?", deletePath);
                //LitePal.deleteAll(MTAPE.class, "path = ?", deletePath);
                //LitePal.deleteAll(MTAPE.class, "POIC = ?", POIC);
                setTitle(tapeshow.this.getResources().getText(R.string.TapeList));
                if (POIType == 0)
                {
                    parseSelectedPathToPOI();
                    refreshCardFromPOI();
                }
                break;
            case R.id.add_pois:
                try {
                    final RecordAudioDialogFragment fragment = RecordAudioDialogFragment.newInstance();
                    fragment.show(getSupportFragmentManager(),RecordAudioDialogFragment.class.getSimpleName());

                    fragment.setOnCancelListener(new RecordAudioDialogFragment.OnAudioCancelListener() {
                        @Override
                        public void onCancel() {
                            fragment.dismiss();

                            SharedPreferences prefTape = getSharedPreferences("sp_name_audio",MODE_PRIVATE);
                            String TapePath = prefTape.getString("audio_path","");

                            Log.w("audio_path_singlepoi: ",TapePath);
                            Uri uri = Uri.parse(TapePath);
                            List<POI> POIs = LitePal.where("poic = ?", POIC).find(POI.class);
                            POI poi = new POI();
                            poi.setTapenum(POIs.get(0).getTapenum() + 1);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tapeshow.this.getResources().getText(R.string.DateAndTime).toString());
                            Date date = new Date(System.currentTimeMillis());
                            poi.updateAll("poic = ?", POIC);
                            MTAPE mtape = new MTAPE();
                            mtape.setPath(TapePath);
                            mtape.setPdfic(POIs.get(0).getIc());
                            mtape.setPoic(POIC);
                            mtape.setTime(simpleDateFormat.format(date));
                            mtape.save();
                            invalidateOptionsMenu();
                            refreshCardFromPOI();
                        }
                    });
//                    Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                    startActivityForResult(intent, REQUEST_CODE_TAPE);
                    if (POIType == 0)
                        refreshCardFromPOI();
                    else if (POIType == 1) refreshCardFromDMBZ();
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MyApplication.getContext(), R.string.TakeTapeError, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAPE){
            Uri uri = data.getData();
            if (DataUtil.getRealPathFromUriForAudio(this, uri).contains("Android/data")){
                Toast.makeText(tapeshow.this, "权限问题，该手机无法正常保存录音文件！", Toast.LENGTH_LONG).show();
            }
            else{
                long time = System.currentTimeMillis();
                if (POIType == 0) {
                    List<POI> POIs = LitePal.where("POIC = ?", POIC).find(POI.class);
                    POI poi = new POI();
                    poi.setTapenum(POIs.get(0).getTapenum() + 1);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tapeshow.this.getResources().getText(R.string.DateAndTime).toString());
                    Date date = new Date(time);
                    poi.updateAll("POIC = ?", POIC);
                    MTAPE mtape = new MTAPE();
                    mtape.setPath(DataUtil.getRealPathFromUriForAudio(this, uri));
                    mtape.setPdfic(POIs.get(0).getIc());
                    mtape.setPoic(POIC);
                    mtape.setTime(simpleDateFormat.format(date));
                    mtape.save();
                }
            }
        }
    }

    private void parseSelectedPathToPOI(){
        List<POI> pois = LitePal.where("POIC = ?", POIC).find(POI.class);
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            for (int i = 0; i < nums.length; i++){
                Log.w(TAG, "parseSelectedPath: " + nums[i]);
                LitePal.deleteAll(MTAPE.class, "POIC = ? and path = ?", POIC, nums[i]);
            }
        }else {
            Log.w(TAG, "parseSelectedPath111: " + deletePath);
            LitePal.deleteAll(MTAPE.class, "POIC = ? and path = ?", POIC, deletePath);
        }
    }

    private void parseSelectedPathToDMBZ(){
        if (deletePath.contains("wslzy")){
            String[] nums = deletePath.split("wslzy");
            Log.w(TAG, "parseSelectedPathToDMBZ: " + nums[0] );
            List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
            String path = pois1.get(0).getTAPEPATH();
            Log.w(TAG, "parseSelectedPathToDMBZ: " + path);
            for (int i = 0; i < nums.length; i++){
                //LitePal.deleteAll(MPHOTO.class, "poic = ? and path = ?", POIC, nums[i]);
                if (DataUtil.appearNumber(path, ".mp3") > 0) {
                    if (DataUtil.appearNumber(path, ".mp3") > 1) {
                        if (path.indexOf(nums[i]) != 0)
                            path = path.replace("|" + nums[i], "");
                        else{
                            path = path.replace(nums[i] + "|", "");
                        }
                    } else {
                        path = "";
                    }
                }
            }
            DMBZ poi = new DMBZ();
            poi.setTAPEPATH(path);
            poi.updateAll("xh = ?", DMXH);
        }else {
            List<DMBZ> pois1 = LitePal.where("xh = ?", DMXH).find(DMBZ.class);
            String path = pois1.get(0).getTAPEPATH();
            if (DataUtil.appearNumber(path, ".mp3") > 0) {
                DMBZ poi = new DMBZ();
                if (DataUtil.appearNumber(path, ".mp3") > 1) {
                    if (path.indexOf(deletePath) != 0)
                        poi.setTAPEPATH(path.replace("|" + deletePath, ""));
                    else
                        poi.setTAPEPATH(path.replace(deletePath + "|", ""));
                } else {
                    poi.setTAPEPATH("");
                }
                poi.updateAll("xh = ?", DMXH);
            }
        }
    }

}
