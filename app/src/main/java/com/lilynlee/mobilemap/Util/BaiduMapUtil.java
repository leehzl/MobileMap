package com.lilynlee.mobilemap.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 封装百度地图的工具类
 * Created by admin on 2016/7/24.
 */
public class BaiduMapUtil {
    private Context mContext;

    private BaiduMap mBaiduMap;

    /*定位相关变量*/
    private LocationClient mLocClient;
    private MyBaiduLocationListener mBaiduLocationListener;
    public boolean isFirstLoc = true;                       //是否是首次定位
    private double mGPSLatitude;                            //保存定位最新的经度
    private double mGPSLongitude;                           //保存定位最新的维度

    public BaiduMapUtil(Context context, BaiduMap baiduMap){
        this.mContext = context;
        this.mBaiduMap = baiduMap;
    }

    /**
     * 注销百度地图定位相关
     */
    public void delete(){
        if (mLocClient != null){
            mLocClient.stop();
        }
        mBaiduMap.setMyLocationEnabled(false);
    }

    /**
     * 初始化百度地图
     */
    public void initBaiduMap(){
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
        mBaiduMap.setMapStatus(msu);
    }

    /**
     * 初始化定位功能
     */
    public void initLocation(){
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mBaiduLocationListener = new MyBaiduLocationListener();
        mLocClient.registerLocationListener(mBaiduLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);            // 打开gps
        option.setCoorType("bd09ll");       // 设置坐标类型
        option.setIsNeedAddress(true);      //是否需要返回一个地址
        option.setScanSpan(2000);           //每隔多长时间定位一次
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位功能监听接口
     */
    private class MyBaiduLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())                 //设置比例尺
                    .latitude(location.getLatitude())               //设置经度
                    .longitude(location.getLongitude()).build();    //设置维度
            mBaiduMap.setMyLocationData(locData);

            /*将location的经纬度保存在变量中*/
            mGPSLatitude = location.getLatitude();
            mGPSLongitude = location.getLongitude();

            if (isFirstLoc) {
                isFirstLoc = false;
                Log.d("test","firstloc");
                centerToLoc(location.getLatitude(),location.getLongitude());
                Toast.makeText(mContext,"地址为："+location.getAddrStr(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将地图界面的中心点，设置为传入的经纬度所在的位置
     * @param latitude double类型的经度
     * @param longtitude double类型的维度
     */
    public void centerToLoc(double latitude, double longtitude){
        LatLng ll = new LatLng(latitude,longtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(msu);
    }

    /**
     * 获取GPS得到的地理位置
     * @return 地理位置
     */
    public LatLng getGPSLocation(){
        LatLng ll = new LatLng(mGPSLatitude, mGPSLongitude);
        return ll;
    }
}
