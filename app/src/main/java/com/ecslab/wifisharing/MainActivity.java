package com.ecslab.wifisharing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ecslab.wifisharing.Map.MapFragment;
import com.ecslab.wifisharing.User.UserFragment;
import com.ecslab.wifisharing.Wifi.WifiFragment;
import com.ecslab.wifisharing.base.BaseActivity;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.event.TabPositionEvent;
import com.ecslab.wifisharing.i.LocationListener;
import com.ecslab.wifisharing.service.WiFiAPService;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;


/**
 *　　┏┓　　  ┏┓+ +
 *　┏┛┻━ ━ ━┛┻┓ + +
 *　┃　　　　　　  ┃
 *　┃　　　━　　    ┃ ++ + + +
 *     ████━████     ┃+
 *　┃　　　　　　  ┃ +
 *　┃　　　┻　　  ┃
 *　┃　　　　　　  ┃ + +
 *　┗━┓　　　┏━┛
 *　　　┃　　　┃　　　　　　　　　　　
 *　　　┃　　　┃ + + + +
 *　　　┃　　　┃
 *　　　┃　　　┃ +  神兽保佑
 *　　　┃　　　┃    代码无bug！　
 *　　　┃　　　┃　　+　　　　　　　　　
 *　　　┃　 　　┗━━━┓ + +
 *　　　┃ 　　　　　　　┣┓
 *　　　┃ 　　　　　　　┏┛
 *　　　┗┓┓┏━┳┓┏┛ + + + +
 *　　　　┃┫┫　┃┫┫
 *　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/18
 * Email：biloba12345@gamil.com
 * Info：主活动，托管三个碎片
 */



public class MainActivity extends BaseActivity implements AMapLocationListener,LocationListener{

    /**
     *   View
     */
    private BottomNavigationView mNavigationView;

    /**
     *   Fragment
     */
    private WifiFragment mWifiFragment;
    private MapFragment mMapFragment;
    private UserFragment mUserFragment;
    /**
     *   Data
     */
    private FragmentManager mFragmentManager;

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    /**
     *   Tag
     */
    private static final String TAG = "MainActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        WiFiAPService.startService(this);
    }

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_main);
        mFragmentManager=getSupportFragmentManager();
        if (bundle!=null) {
            mWifiFragment= (WifiFragment) mFragmentManager
                    .findFragmentByTag(WifiFragment.class.getName());;
            mMapFragment= (MapFragment) mFragmentManager
                    .findFragmentByTag(MapFragment.class.getName());
            mUserFragment= (UserFragment) mFragmentManager
                    .findFragmentByTag(UserFragment.class.getName());
            mFragmentManager.beginTransaction()
                    .show(mWifiFragment)
                    .hide(mMapFragment)
                    .hide(mUserFragment)
                    .commit();
        }else {
            mWifiFragment=WifiFragment.newInstance();
            mMapFragment=MapFragment.newInstance();
            mUserFragment=UserFragment.newInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.container, mWifiFragment, WifiFragment.class.getName())
                    .add(R.id.container, mMapFragment, MapFragment.class.getName())
                    .add(R.id.container, mUserFragment, UserFragment.class.getName())
                    .hide(mMapFragment)
                    .hide(mUserFragment)
                    .commit();
        }
    }

    @Override
    protected void initView() {
        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        initeNavigationMenu();

    }

    @Override
    protected void setListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_wifi:
                        mFragmentManager.beginTransaction()
                                .hide(mMapFragment)
                                .hide(mUserFragment)
                                .show(mWifiFragment)
                                .commit();
                        return true;
                    case R.id.navigation_map:
                        mFragmentManager.beginTransaction()
                                .hide(mWifiFragment)
                                .hide(mUserFragment)
                                .show(mMapFragment)
                                .commit();
                        return true;
                    case R.id.navigation_user:
                        mFragmentManager.beginTransaction()
                                .hide(mMapFragment)
                                .hide(mWifiFragment)
                                .show(mUserFragment)
                                .commit();
                        return true;
                }
                return false;
            }

        });
    }

    @Override
    protected void initData() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        initLoc();
    }

    @Override
    protected void setData() {
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void onEventComing(EventCenter center) {
        Log.d(TAG, "onEventComing: ");
        if (center.getData()instanceof TabPositionEvent) {
            mNavigationView.setSelectedItemId(R.id.navigation_map);
        }
    }

    private void initeNavigationMenu(){
        Menu menu=mNavigationView.getMenu();
        for(int i=0;i<menu.size();i++){
            MenuItem item=menu.getItem(i);
            switch (item.getItemId()) {
                case R.id.navigation_wifi:
                    item.setIcon(getIcon(GoogleMaterial.Icon.gmd_network_wifi, Color.parseColor("#a7c7f7")));
                    break;
                case R.id.navigation_map:
                    item.setIcon(getIcon(GoogleMaterial.Icon.gmd_map, Color.parseColor("#a7c7f7")));
                    break;
                case R.id.navigation_user:
                    item.setIcon(getIcon(GoogleMaterial.Icon.gmd_person, Color.parseColor("#a7c7f7")));
                    break;
            }
        }
    }
    
    
    @Override
    protected void onDestroy() {
        WiFiAPService.stopService(this);
        mLocationClient.onDestroy();
        super.onDestroy();
    }

    //定位
    private void initLoc() {
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (mMapFragment != null) {
            mMapFragment.locationChanged(location);
        }

        if (mWifiFragment != null) {
            mWifiFragment.locationChanged(location);
        }
    }

    @Override
    public void startLoc() {
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void stopLoc() {
        mLocationClient.stopLocation();
    }
}

