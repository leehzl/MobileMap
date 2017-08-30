package com.lilynlee.mobilemap.activity;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.core.geometry.Envelope;
import com.lilynlee.mobilemap.R;

import java.io.File;

public class ArcgisActivity extends AppCompatActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arcgis_layout);
        mMapView = (MapView) findViewById(R.id.arcgis_map);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ArcGIS/china_brp.tpk";
        File file = new File(path);
        if (file.exists()){
            ArcGISLocalTiledLayer local = new ArcGISLocalTiledLayer(path);
            mMapView.addLayer(local);
        }else{
            Toast.makeText(this,"没有本地文件",Toast.LENGTH_SHORT).show();
        }
        Log.d("test","比例尺"+mMapView.getScale());
        Envelope env = new Envelope(12957628.58241, 4864247.2803126, 12958114.4225065, 4864490.20036087);//范围
        mMapView.setExtent(env);//设置地图显示范围
        mMapView.setScale(295828763);//当前显示的比例尺
        mMapView.setResolution(9783.93962049996);//设置当前显示的分辨率
//上面三个方法都可以改变地图的显示范围，在代码中是不会同时使用的
        mMapView.setMapBackground(0xffffffff, Color.TRANSPARENT, 0, 0);//设置地图背景
        mMapView.setAllowRotationByPinch(true); //是否允许使用Pinch方式旋转地图
    }
}
