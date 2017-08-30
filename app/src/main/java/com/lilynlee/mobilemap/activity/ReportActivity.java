package com.lilynlee.mobilemap.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lilynlee.mobilemap.R;
import com.lilynlee.mobilemap.object.ReportItem;
import com.lilynlee.mobilemap.Util.TakePhotoUtil;
import com.lilynlee.mobilemap.Util.AnimationUtil;
import com.lilynlee.mobilemap.Util.BaiduMapUtil;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Button mReport_button;      //上报按钮
    private Button mSave_button;        //保存按钮
    private ImageButton mCamera_button; //拍照功能按钮
    private ImageButton mBackMyLoc;     //回到当初定位的位置按钮
    private ImageView mMapCenter;       //地图中心的箭头，每移动地图，该箭头都会跳动一下
    private TextView mLocationName;     //显示地理位置名称
    private EditText mDescirbe;         //填写描述信息
    private ImageView mShowPicture;     //显示拍照的照片

    /*Baidu地图工具类库*/
    private BaiduMapUtil mBaiduMapUtil;

    /*要上报的信息*/
    private double mReportLatitude = 0;         //需要上报的经度坐标
    private double mReportLongitude = 0;        //需要上报的维度坐标
    private String mReportLocationName = null;  //需要上报的地理名称
    private String mReportDescribe = null;      //描述信息
    private String mReportPictureUri = null;    //需要上报的图片uri，绝对路径
    private String mReportFileName = "";        //需要上报的文件名称：2014-12-20 29:04

    /*要上报的图片*/
    private Uri mImageURI = null;
    private static final int TAKE_PHOTO = 1;

    /*地理位置反编译相关*/
    private GeoCoder mGeoCoder;

    /*动画工具类*/
    AnimationUtil mAnimation = new AnimationUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.report_layout);

        initView();
        initGeoCode();          //初始化地理反编译
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMapUtil.delete();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 初始化反编译，并且设置反编译的回调函数动作
     */
    private void initGeoCode() {
        mGeoCoder = GeoCoder.newInstance();

        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

            //反地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {}

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

                if (result == null||result.error!= SearchResult.ERRORNO.NO_ERROR){
                    //没有检测到结果
                    Toast.makeText(ReportActivity.this,"没有定位到位置信息",Toast.LENGTH_SHORT).show();
                    mLocationName.setText("请再次拖动地图，重新获取位置信息");
                }
                mLocationName.setText("地址:"+result.getAddress());
                mReportLocationName = result.getAddress();
            }
        };
        mGeoCoder.setOnGetGeoCodeResultListener(listener);

    }

    private void initView() {
        /*控件的初始化*/
        mMapView = (MapView) findViewById(R.id.report_map);
        mBaiduMap = mMapView.getMap();
        mReport_button = (Button) findViewById(R.id.report_report);
        mSave_button = (Button) findViewById(R.id.report_save);
        mCamera_button = (ImageButton) findViewById(R.id.report_takephoto);
        mBackMyLoc = (ImageButton) findViewById(R.id.report_back_to_myloc);
        mLocationName = (TextView) findViewById(R.id.report_location_name);
        mMapCenter = (ImageView) findViewById(R.id.report_mapcenter);
        mShowPicture = (ImageView) findViewById(R.id.report_showpicture);
        mDescirbe = (EditText) findViewById(R.id.report_describe);

        /*按钮们的监听*/
        mReport_button.setOnClickListener(this);
        mSave_button.setOnClickListener(this);
        mCamera_button.setOnClickListener(this);
        mBackMyLoc.setOnClickListener(this);

        /*创建BaiduMap工具类*/
        mBaiduMapUtil = new BaiduMapUtil(ReportActivity.this,mBaiduMap);

        mBaiduMapUtil.initBaiduMap();

        /*定位*/
        mBaiduMapUtil.initLocation();

        /*
        * 当拖动地图时候会触发该事件监听，
        * 将地图中心点的坐标反编译成地理信息位置
        */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {}

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {}

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLng ll = mBaiduMap.getMapStatus().target;
                mReportLatitude = ll.latitude;
                mReportLongitude = ll.longitude;
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
                mAnimation.SkipAnimation(mMapCenter);
            }
        });
        /*判断是否有列表修改传进来的数据*/
        Intent intent = getIntent();
        ReportItem intent_item = (ReportItem) intent.getSerializableExtra("item");
        if (intent_item != null){
            mBaiduMapUtil.isFirstLoc = false;               //为了不让初始化LocationListener时候，把坐标定在GPS的坐标中
            String image_uri = intent_item.getImage_uri();
            String location_name = intent_item.getLocation();
            String describe = intent_item.getDescribe();
            String file_name = intent_item.getFile_name();
            float latitude = intent_item.getLatitude();
            float longitude = intent_item.getLongitude();
            mReportFileName = file_name;
            mReportLatitude = Double.parseDouble(latitude+"");
            mReportLongitude = Double.parseDouble(longitude+"");
            mReportDescribe = describe;
            mReportPictureUri = image_uri;
            mDescirbe.setText(mReportDescribe);
            mLocationName.setText(location_name);
            mBaiduMapUtil.centerToLoc(mReportLatitude,mReportLongitude);
            Bitmap bmp = BitmapFactory.decodeFile(mReportPictureUri);
            mShowPicture.setImageBitmap(bmp);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.report_back_to_myloc:
                mAnimation.ButtonClickSmallAnimation(mBackMyLoc);
                //返回到定位的位置,并将定位的位置经纬度存到需要上传的信息中
                LatLng ll = mBaiduMapUtil.getGPSLocation();
                mReportLatitude = ll.latitude;
                mReportLongitude = ll.longitude;
                Log.d("test","backtomyloc latitude:"+mReportLatitude);
                Log.d("test","backtomyloc longitude:"+mReportLongitude);
                mBaiduMapUtil.centerToLoc(mReportLatitude,mReportLongitude);
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
                break;
            case R.id.report_takephoto:
                //拍照
                mAnimation.ButtonClickSmallAnimation(mCamera_button);
                TakePhotoUtil takePhotoUtil = new TakePhotoUtil(this);
                String imageName = takePhotoUtil.getCurrentTime();                  //根据时间获取图片名称
                String imageDir = takePhotoUtil.getAbsoluteDir();                   //获取图片存储的论净
                mImageURI = takePhotoUtil.createPhotoFile(imageDir,imageName);      //根据路径和名称创建一个图片文件并返回一个文件的uri
                mReportPictureUri = mImageURI.getPath();                            //保存图片的uri
                /*调用系统的拍照功能*/
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI);
                startActivityForResult(intent,TAKE_PHOTO);
                break;
            case R.id.report_save:
                //保存
                /////////////////////////////////////////////////////////
                /*              临时调试用的到时候要删掉                    */
                /////////////////////////////////////////////////////////
/*
                Toast.makeText(this,"保存成功！",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(ReportActivity.this,MainActivity.class);
                startActivity(intent1);
                finish();
*/



                /////////////////////////////////////////////////////////
                /*              以下代码到时候要恢复！！                    */
                /////////////////////////////////////////////////////////
                //saveFile();

//                保存成功后，转到跳转到列表模块
                Intent finish_intnet = new Intent(ReportActivity.this,ReportListActivity.class);
                startActivity(finish_intnet);
                finish();
                break;
            case R.id.report_report:
                //上报
                break;
            default:
                break;
        }
    }

    /**
     * 上报文件保存
     * 文件名称：案件-2012-12-12 12:12
     * 案件描述
     * 位置名称
     * 位置经度
     * 位置维度
     * 图片uri
     */
    private void saveFile() {
        if (mReportPictureUri == null){
            mReportPictureUri = "";
        }
        mReportDescribe = mDescirbe.getText().toString().trim();
        if (mReportFileName == ""){
            mReportFileName = getCurrentDate("yyyy-MM-dd HH:mm");
        }
        SharedPreferences.Editor editor = getSharedPreferences("上报-"+mReportFileName,MODE_PRIVATE).edit();
        editor.putString(ReportItem.FILE_NAME,mReportFileName);
        editor.putString(ReportItem.DESCRIBE,mReportDescribe);
        editor.putFloat(ReportItem.LATITUDE,Float.parseFloat(mReportLatitude+""));
        editor.putFloat(ReportItem.LONGITUDE,Float.parseFloat(mReportLongitude+""));
        editor.putString(ReportItem.LOCATION_NAME,mReportLocationName);
        editor.putString(ReportItem.IMAGE_URI,mReportPictureUri);
        editor.commit();
        Toast.makeText(this,"保存成功！",Toast.LENGTH_SHORT).show();
    }

    /**
     * 根据正则表达式获取当地时间
     * @param s 正则表达式
     * @return 返回时间 2016-12-12 12:12
     */
    private String getCurrentDate(String s) {
        SimpleDateFormat df = new SimpleDateFormat(s);//设置日期格式
        return df.format(new Date(System.currentTimeMillis()));// new Date()为获取当前系统时间
    }

    /**
     * 调用拍照功能后，触发该函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(mImageURI));
                        mShowPicture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}