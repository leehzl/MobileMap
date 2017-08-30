package com.lilynlee.mobilemap.activity;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.lilynlee.mobilemap.view.ArcMenu;
import com.lilynlee.mobilemap.R;
import com.lilynlee.mobilemap.Util.AnimationUtil;
import com.lilynlee.mobilemap.Util.BaiduMapUtil;

import java.lang.reflect.Method;

public class MainActivity extends ActionBarActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private ImageButton mBackToMyLoc;

    /*Toolbar*/
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ListView listView;
    private String[] strs = {"test1","test2","test3","test4"};
    private ArrayAdapter arrayAdapter;

    /*Baidu地图工具类，封装了定位等功能*/
    private BaiduMapUtil mBaiduMapUtil;

    /*Menu对象*/
    private ArcMenu mArcMenu;

    /*动画工具*/
    private AnimationUtil mAnimation = new AnimationUtil();


    /*Toolbar相关*/
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.main_layout);

        //初始化Toolbar
        initToolBar();

        //初始化主界面
        initView();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MobileMap");//设置标题
        //toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Toast.makeText(MainActivity.this,"drawer open",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Toast.makeText(MainActivity.this,"drawer close",Toast.LENGTH_SHORT).show();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    protected boolean toggleDrawerLayout(){
        //如果左边的已打开，则关闭左边的，不进行后续操作
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return true;
        }
        //如果左边的没打开，右边的打开了关闭，关闭了打开
        if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.closeDrawer(Gravity.END);
        } else {
            mDrawerLayout.openDrawer(Gravity.END);
        }
        return true;
    }

    public void initView() {
        mMapView = (MapView) findViewById(R.id.main_map);
        mArcMenu = (ArcMenu) findViewById(R.id.main_menu);  //初始化菜单
        mBackToMyLoc = (ImageButton) findViewById(R.id.main_back_to_myloc);
        mBaiduMap = mMapView.getMap();                      //获取Baidu地图对象
        mBaiduMapUtil = new BaiduMapUtil(this,mBaiduMap);   //创建Baidu地图工具类
        mBaiduMapUtil.initBaiduMap();                       //初始化Baidu地图

        mBaiduMapUtil.initLocation();                       //初始化定位功能

        /*如果菜单打开状态，只要触碰地图则将菜单关闭*/
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (mArcMenu.isMenuOpen()){
                    mArcMenu.toggleMenu(400);
                }
            }
        });

        /*menuItem点击事件处理*/
        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemCliceListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position){
                    case 1:
//                        2D地图
                        break;
                    case 2:
//                        卫星、遥感地图
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                Intent arcgis_intent = new Intent(MainActivity.this,ArcgisActivity.class);
                                startActivity(arcgis_intent);
                            }
                        }, 280);
                        break;
                    case 3:
//                        上报
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                //execute the task
                                Intent report_intent = new Intent(MainActivity.this,ReportActivity.class);
                                startActivity(report_intent);
                            }
                        }, 280);

                        break;
                    case 4:
//                        列表
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                Intent list_intent = new Intent(MainActivity.this,ReportListActivity.class);
                                startActivity(list_intent);
                            }
                        }, 280);

                        break;
                    default:
                        break;
                }
            }
        });

        /**
         * 回到定位的位置
         */
        mBackToMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnimation.ButtonClickSmallAnimation(mBackToMyLoc);
                LatLng gps_ll = mBaiduMapUtil.getGPSLocation();
                mBaiduMapUtil.centerToLoc(gps_ll.latitude,gps_ll.longitude);
            }
        });
    }

    /**
     * 为了让溢出的菜单显示出图标，我们需要重新一个方法，使用反射让其显示出来
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.toolbar_setting1:
                Toast.makeText(MainActivity.this,"setting 1",Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_setting2:
                Toast.makeText(MainActivity.this,"setting 2",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        /*关闭定位*/
        mBaiduMapUtil.delete();
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
}
