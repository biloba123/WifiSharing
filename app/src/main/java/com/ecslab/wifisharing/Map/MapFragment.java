package com.ecslab.wifisharing.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.Wifi.bean.Hotspot;
import com.ecslab.wifisharing.base.BaseFragment;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.i.LocationListener;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 　　┏┓　　  ┏┓+ +
 * 　┏┛┻━ ━ ━┛┻┓ + +
 * 　┃　　　　　　  ┃
 * 　┃　　　━　　    ┃ ++ + + +
 * ████━████     ┃+
 * 　┃　　　　　　  ┃ +
 * 　┃　　　┻　　  ┃
 * 　┃　　　　　　  ┃ + +
 * 　┗━┓　　　┏━┛
 * 　　　┃　　　┃
 * 　　　┃　　　┃ + + + +
 * 　　　┃　　　┃
 * 　　　┃　　　┃ +  神兽保佑
 * 　　　┃　　　┃    代码无bug！
 * 　　　┃　　　┃　　+
 * 　　　┃　 　　┗━━━┓ + +
 * 　　　┃ 　　　　　　　┣┓
 * 　　　┃ 　　　　　　　┏┛
 * 　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　┃┫┫　┃┫┫
 * 　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/20
 * Email：biloba12345@gamil.com
 * Info：
 */

public class MapFragment extends BaseFragment  implements  LocationSource {

    /**
    *   View
    */
    private Toolbar mToolbar;
    //显示地图需要的变量
    private MapView mapView;//地图控件
    private AMap aMap;//地图对象

    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    /**
    *   Fragment
    */

    /**
    *   Data
    */
    private List<Marker> mMarkerList=new ArrayList<>();
    private List<Marker> mTempMarkerList=new ArrayList<>();

    private LocationListener mLocationListener;

    /**
    *   Tag
    */
    private static final String TAG = "MapFragment";

    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLocationListener= (LocationListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLocationListener=null;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_map,container,false);
        //显示地图
        mapView = (MapView) view.findViewById(R.id.map);
        //必须要写
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    protected void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolbar(mToolbar,getString(R.string.title_map),false);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        //获取地图对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }


        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        //縮放按鈕
        settings.setZoomControlsEnabled(false);
        settings.setAllGesturesEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);


        //定位的小图标
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);

        //开始定位
       mLocationListener.startLoc();


    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }


    public void locationChanged(AMapLocation amapLocation) {
        Logger.d("locationChanged");
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                Log.d(TAG, "onLocationChanged: 定位");
                //定位成功回调信息，设置相关消息
//                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                amapLocation.getLatitude();//获取纬度
//                amapLocation.getLongitude();//获取经度
//                amapLocation.getAccuracy();//获取精度信息
//                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                amapLocation.getCountry();//国家信息
//                amapLocation.getProvince();//省信息
//                amapLocation.getCity();//城市信息
//                amapLocation.getDistrict();//城区信息
//                amapLocation.getStreet();//街道信息
//                amapLocation.getStreetNum();//街道门牌号信息
//                amapLocation.getCityCode();//城市编码
//                amapLocation.getAdCode();//地区编码
//                amapLocation.getAoiName();//获取当前定位点的AOI信息
//                amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//                amapLocation.getFloor();//获取当前室内定位的楼层
                //获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    Log.d(TAG, "onLocationChanged: "+amapLocation.getLatitude()+"  "+amapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);

                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(getActivity(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }

                //添加图钉
                addMarkerOptions(amapLocation);


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

//                Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void addMarkerOptions(final AMapLocation amapLocation){

        BmobQuery<Hotspot> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereNear("point", new BmobGeoPoint(amapLocation.getLongitude(),amapLocation.getLatitude()));
        bmobQuery.setLimit(20);    //获取最接近用户地点的10条数据
        bmobQuery.findObjects(new FindListener<Hotspot>() {
            @Override
            public void done(List<Hotspot> object, BmobException e) {
                if(e==null){
                    Log.d(TAG, "done: "+"查询成功：共" + object.size() + "条数据。");
                    for (Hotspot hotspot : object) {
                        MarkerOptions options = new MarkerOptions();
                        //图标
//        ArrayList<BitmapDescriptor> bitmapDescriptors=new ArrayList<>();
//        bitmapDescriptors.add(BitmapDescriptorFactory.fromResource(R.mipmap.hotspot));

//        options.icons(bitmapDescriptors);
                        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.hotspot));
                        //位置
                        BmobGeoPoint p=hotspot.getPoint();
                        options.position(new LatLng(p.getLatitude(), p.getLongitude()));
                        //标题
                        options.title(hotspot.getLocation());
                        //子标题
                        options.snippet("相距："+
                                (int)AMapUtils.calculateLineDistance(new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude()),
                                        options.getPosition())+"m");
                        mTempMarkerList.add(aMap.addMarker(options));
                    }

                    //摧毁标记
                    Log.d(TAG, "addMarkerOptions: 标签数："+mMarkerList.size());
                    for (int i = 0; i < mMarkerList.size(); i++) {
                        mMarkerList.get(i).remove();
                    }
                    mMarkerList.clear();
                    mMarkerList.addAll(mTempMarkerList);
                    mTempMarkerList.clear();
                }else{
                    e.printStackTrace();
                }
            }
        });

    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }


}
