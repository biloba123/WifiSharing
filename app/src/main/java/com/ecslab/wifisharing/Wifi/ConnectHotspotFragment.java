package com.ecslab.wifisharing.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.Wifi.bean.Hotspot;
import com.ecslab.wifisharing.base.BaseFragment;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.event.TabPositionEvent;
import com.ecslab.wifisharing.event.WifiChangeEvent;
import com.ecslab.wifisharing.tool.SolidRVBaseAdapter;
import com.ecslab.wifisharing.tool.WifiAdmin;
import com.ecslab.wifisharing.tool.WifiHotUtil;
import com.ecslab.wifisharing.view.LVWifi;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ecslab.wifisharing.R.string.connecting;
import static com.ecslab.wifisharing.tool.WifiHotUtil.WIFI_AP_STATE_DISABLED;
import static com.ecslab.wifisharing.tool.WifiHotUtil.WIFI_AP_STATE_ENABLED;

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
 * Date：2017/3/18
 * Email：biloba12345@gamil.com
 * Info：
 */

/**
 * You may think you know what the following code does.
 * But you dont. Trust me.
 * Fiddle with it, and youll spend many a sleepless
 * night cursing the moment you thought youd be clever
 * enough to "optimize" the code below.
 * Now close this file and go play with something else.
 */
/**
 * 你可能会认为你读得懂以下的代码。但是你不会懂的，相信我吧。
 * 要是你尝试玩弄这段代码的话，你将会在无尽的通宵中不断地咒骂自己为什么会认为自己聪明到可以优化这段代码。
 * 现在请关闭这个文件去玩点别的吧。
 */

public class ConnectHotspotFragment extends BaseFragment {

    /**
    *   View
    */
    private android.widget.Switch shareswitch;
    private android.support.v7.widget.RecyclerView wifirecyclerview;
    /**
    *   Fragment
    */

    /**
    *   Data
    */
    private WifiAdmin mWifiAdmin;
    private LVWifi mLVWifi;
    private List<ScanResult> mScanResultList=new ArrayList<>();
    private SolidRVBaseAdapter mAdapter;
    private boolean mIsConnectingWifi;
    private boolean mIsOnSpeeding;
    private WifiHotUtil mWifiHotUtil;
    /**
    *   Tag
    */
    private static final String TAG = "ConnectHotspotFragment";
    private TextView mWifiStateText;
    private ImageView mLevelImg;
    private TextView mWifiNameText;
    private TextView mNoWifiText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mSpeedLl;
    private TextView mPostText;
    private TextView mGetText;


    public static ConnectHotspotFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ConnectHotspotFragment fragment = new ConnectHotspotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mWifiAdmin.againGetWifiInfo();
        mWifiAdmin.againGetWifiConfigurations();
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWifiAdmin=new WifiAdmin(getActivity());
        //开始扫描
        mWifiAdmin.scan();
        //注册接收器
        IntentFilter filter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mReceiver, filter);

        View view=inflater.inflate(R.layout.fragment_connect_hotspot,container,false);
        return view;
    }

    @Override
    protected void initView(View view) {
        this.wifirecyclerview = (RecyclerView) view.findViewById(R.id.wifi_recycler_view);
        this.shareswitch = (Switch) view.findViewById(R.id.share_switch);
        mLVWifi = (LVWifi) view.findViewById(R.id.lv_wifi);
        mLVWifi.setViewColor(Color.parseColor("#007fff"));
        mWifiStateText = (TextView) view.findViewById(R.id.wifi_state_text);
        mLevelImg = (ImageView) view.findViewById(R.id.level_img);
        mWifiNameText = (TextView) view.findViewById(R.id.wifi_name_text);
        mNoWifiText = (TextView) view.findViewById(R.id.no_wifi_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE);
        mSpeedLl = (LinearLayout) view.findViewById(R.id.speed_ll);
        mPostText = (TextView) view.findViewById(R.id.post_text);
        mGetText = (TextView) view.findViewById(R.id.get_text);
    }

    @Override
    protected void setListener() {
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mWifiHotUtil.getWifiAPState()==WIFI_AP_STATE_DISABLED) {
                    mWifiAdmin.scan();
                    mWifiAdmin.againGetWifiInfo();
                    updateConnectedWifi();
                }else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        
        //打開地圖
        mNoWifiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault()
                        .post(new EventCenter<>(0,new TabPositionEvent()));
            }
        });
    }

    @Override
    protected void initData() {
        mWifiHotUtil=WifiHotUtil.getInstance(getActivity());
        mAdapter=new SolidRVBaseAdapter<ScanResult>(getActivity(), mWifiAdmin.getScanResultList()) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, final ScanResult scanResult) {
                holder.setText(R.id.name,WifiHotUtil.getWifiName(getActivity(), scanResult.SSID));
                //有锁？
//                Log.d(TAG, "onBindDataToView: "+scanResult.SSID+"\n"+scanResult.capabilities);
                final boolean isLocked=scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
                        scanResult.capabilities.contains("WEP");
                if(isLocked){
                    holder.setImage(R.id.level,R.drawable.wifi_signal_lock);
                }else {
                    holder.setImage(R.id.level,R.drawable.wifi_signal_open);
                }
                //信号强度
                ImageView levelImg=holder.getView(R.id.level);
                levelImg.setImageLevel(WifiManager.calculateSignalLevel(scanResult.level,5));
                holder.setImage(R.id.tag, scanResult.SSID.endsWith(getString(R.string.wifi_special))?
                                getIcon(GoogleMaterial.Icon.gmd_stars,Color.parseColor("#fcc902")):
                        getIcon(GoogleMaterial.Icon.gmd_info_outline, Color.LTGRAY));
            }

            @Override
            public int getItemCount() {
                return super.getItemCount()+1;
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_wifi;
            }


            @Override
            protected void onItemClick(int position,ScanResult scanResult) {
                super.onItemClick(position,scanResult);
                final boolean isLocked=scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
                        scanResult.capabilities.contains("WEP");
                Log.d(TAG, "onItemClick: 连接");

                if (mWifiHotUtil.getWifiAPState()==WIFI_AP_STATE_ENABLED) {
                    Toasty.warning(getActivity(), getString(R.string.close_ap), Toast.LENGTH_SHORT).show();
                }else {
                    if (!(scanResult.SSID.equals(mWifiAdmin.getSSID())
                            &&scanResult.BSSID.equals(mWifiAdmin.getBSSID()))) {
                        Log.d(TAG, "onItemClick: 不是当前连接wifi");
                        //未开启网卡则开启
                        if (!mWifiAdmin.isNetCardFriendly()) {
                            mWifiAdmin.openNetCard();
                        }
                        Log.d(TAG, "onClick: 未配置");
                        WifiConfiguration configuration=mWifiAdmin.isExsits(scanResult.SSID);
                        if (configuration == null) {
                                if (!scanResult.SSID.endsWith(getString(R.string.wifi_special))) {
                                if (isLocked) {
                                    showEditPassDialog(scanResult);
                                }else {
                                    connecting(scanResult,null,1);
                                }
                            }else {
                                showConnectSharedWifi(scanResult);
                            }
                        }else {
                            Log.d(TAG, "onClick: 已配置\n"+configuration);
                            mIsConnectingWifi=true;
                            mWifiStateText.setText(getString(connecting)+ scanResult.SSID);
                            //有配置直接连接
                            if (!mWifiAdmin.connectConfiguration(configuration)) {
                                showErrorToast(R.string.connect_error);
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void setData() {
        if (mWifiAdmin.isNetCardFriendly()) {
            //wifi开启并以连接
            if (mWifiAdmin.isConnected()) {
                wifiConnected();
                updateConnectedWifi();
            }//未连接
            else {
                wifiUnConnect();
            }
        }else{
            //未打开
            wifiClosed();
        }

        wifirecyclerview.setAdapter(mAdapter);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
//        llm.setSmoothScrollbarEnabled(true);
        wifirecyclerview.setLayoutManager(llm);
//        wifirecyclerview.setHasFixedSize(false);
//        wifirecyclerview.setNestedScrollingEnabled(false);
        checkWifi();
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void onEventComing(EventCenter center) {
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        mIsOnSpeeding=false;
        super.onDestroy();
    }


    //监听wifi状态变化
    private BroadcastReceiver mReceiver = new BroadcastReceiver (){
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: "+intent.getAction());
            String action=intent.getAction();
            //监听wifi打开关闭
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        showInfoToast("Wifi关闭");
                        mIsConnectingWifi=false;
                        mWifiAdmin.againGetWifiInfo();
                        wifiClosed();
                        EventBus.getDefault()
                                .post(new EventCenter<>(0,new WifiChangeEvent(false)));
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        showInfoToast("Wifi打开");
                        EventBus.getDefault()
                                .post(new EventCenter<>(0,new WifiChangeEvent(true)));
                        break;
                }
            }

            //扫描结果出来
            if (intent.getAction()== WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                Logger.d("扫描结果");
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
//                mWifiAdmin.getScanResult();
                if (mWifiAdmin.getScanResultList()!=null) {
//                        Log.d(TAG, "onReceive: ----------------------------------------------\n"+mWifiAdmin.getScanResult());
                        mAdapter.notifyDataSetChanged();
//                    Log.d(TAG, "onReceive: "+mAdapter.getItemCount());
                        checkWifi();
                }

            }

            //连接上WIFI
            if(intent.getAction()==WifiManager.NETWORK_STATE_CHANGED_ACTION){
                Parcelable parcelable=intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelable != null) {
                    NetworkInfo networkInfo= (NetworkInfo) parcelable;
                    NetworkInfo.State state=networkInfo.getState();
                    if (state== NetworkInfo.State.CONNECTED) {
                        mWifiAdmin.againGetWifiInfo();
                        mIsConnectingWifi=false;
                        showSuccToast(mWifiAdmin.getSSID()+"已连接");
                        wifiConnected();
                        updateConnectedWifi();
                    }else{
                        wifiUnConnect();
                    }
                }

            }

        }

    };

    //wifi关闭
    private void wifiClosed(){
        mLevelImg.setVisibility(View.GONE);
        mWifiNameText.setVisibility(View.GONE);
        mSpeedLl.setVisibility(View.GONE);
        mIsOnSpeeding=false;

        mLVWifi.setVisibility(View.VISIBLE);
        mLVWifi.stopAnim();
        mWifiStateText.setVisibility(View.VISIBLE);
        mWifiStateText.setText(R.string.wifi_closed);
    }

    //wifi未连接
    private void wifiUnConnect(){
        mLevelImg.setVisibility(View.GONE);
        mWifiNameText.setVisibility(View.GONE);
        mSpeedLl.setVisibility(View.GONE);
        mIsOnSpeeding=false;

        mLVWifi.setVisibility(View.VISIBLE);
        mLVWifi.startAnim(6000);
        mWifiStateText.setVisibility(View.VISIBLE);
        if (!mIsConnectingWifi) {
            mWifiStateText.setText(R.string.wifi_unconnect);
        }
    }

    //wifi连接
    private void wifiConnected(){
        mLVWifi.setVisibility(View.GONE);
        mLVWifi.stopAnim();
        mWifiStateText.setVisibility(View.GONE);

        mLevelImg.setVisibility(View.VISIBLE);
        mWifiNameText.setVisibility(View.VISIBLE);
        mSpeedLl.setVisibility(View.VISIBLE);
        mIsOnSpeeding=true;
        runSpeedThred();
    }

    //更新连接Wifi信息
    private void updateConnectedWifi(){
//                mLevelImg.setImageLevel();
//        Log.d(TAG, "updateConnectedWifi: "+mWifiAdmin.getWifiInfo());
        mWifiNameText.setText(mWifiAdmin.getSSID());
        //信号强度
        mLevelImg.setImageLevel(WifiManager.calculateSignalLevel(mWifiAdmin.getRssi(),5));
    }

    //检查附近是否有wifi
    private void checkWifi(){
        if (mAdapter.getItemCount()>0) {
            mNoWifiText.setVisibility(View.GONE);
            wifirecyclerview.setVisibility(View.VISIBLE);
        }else{
            mNoWifiText.setVisibility(View.VISIBLE);
            wifirecyclerview.setVisibility(View.GONE);
        }
    }

    //正在连接wifi
    private void connecting(ScanResult result,String pass,int type){
        mIsConnectingWifi=true;
        mWifiStateText.setText(getString(connecting)+result.SSID);
        int wcgID= mWifiAdmin.connectWifi(result.SSID,
                pass,type);
        Log.d(TAG, "connecting: "+wcgID);
        if (wcgID==-1) {
            showErrorToast(R.string.connect_error);
        }
    }

    private void showEditPassDialog(final ScanResult result){
        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_edit_wifi_pass,null);
        ((TextView) v.findViewById(R.id.name))
                .setText(result.SSID);
        final EditText editText = (EditText) v.findViewById(R.id.edit_text);

        AlertDialog dialog=new AlertDialog.Builder(getActivity())
                .setView(v)
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String psd=editText.getText().toString();
                        if (!psd.isEmpty()) {
                            if (result.capabilities.contains("WEP")) {
                                connecting(result,psd,2);
                            }else {
                                connecting(result,psd,3);
                            }
                        }else {
                            showErrorToast(R.string.empty_password);
                        }
                    }
                }).create();
        dialog.show();

        final Button positiveBtn=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()<8) {
                    positiveBtn.setEnabled(false);
                }else {
                    positiveBtn.setEnabled(true);
                }
            }
        });

    }

    private void showConnectSharedWifi(final ScanResult result){
        View v=LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_connect_sharing_wifi,null);
        ((TextView) v.findViewById(R.id.name_text))
                .setText(WifiHotUtil.getWifiName(getActivity(), result.SSID));

        AlertDialog dialog=new AlertDialog.Builder(getActivity())
                .setView(v)
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectSharedWifi(result);
                    }
                }).create();
        dialog.show();
    }


    private void connectSharedWifi(final ScanResult result){
        BmobQuery<Hotspot> query = new BmobQuery<>();
        query.addWhereEqualTo("ssid", result.SSID);
        Log.d(TAG, "connectSharedWifi: ");
        //执行查询方法
        query.findObjects(new FindListener<Hotspot>() {
            @Override
            public void done(List<Hotspot> object, BmobException e) {
                Log.d(TAG, "done: ");
                if(e==null){
                    if (object.size()>0) {
                        Log.d(TAG, "done: ---------------------------------------------");
                        for (Hotspot hotspot : object) {
                            Log.d(TAG, "done: "+hotspot.getSsid()+" "+hotspot.getPassword());
                            if (result.capabilities.contains("WEP")) {
                                connecting(result,hotspot.getPassword(),2);
                            }else {
                                connecting(result,hotspot.getPassword(),3);
                            }
                        }
                    }else {
                        showInfoToast(R.string.no_hotspot_info);
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    showErrorToast(R.string.query_hotspot_error);
                }
            }
        });
    }


    //是否开启测速
    private boolean isOnSpeeding(){
        return mIsOnSpeeding;
    }

    //测速线程
    public void runSpeedThred(){
        final int count = 2;
        Observable.create(new Observable.OnSubscribe<int[]>() {
            @Override
            public void call(Subscriber<? super int[]> subscriber) {
                try {
                    int[] speeds=new int[2];
                    long total_get_data = TrafficStats.getTotalRxBytes();
                    long total_post_data=TrafficStats.getTotalTxBytes();
                    while (isOnSpeeding()){
                        long traffic_data =TrafficStats.getTotalRxBytes() - total_get_data;
                        total_get_data = TrafficStats.getTotalRxBytes();
                        speeds[0]=(int)traffic_data /count;
                        traffic_data =TrafficStats.getTotalTxBytes() - total_post_data;
                        total_post_data = TrafficStats.getTotalTxBytes();
                        speeds[1]=(int)traffic_data /count;
                        subscriber.onNext(speeds);
                        Thread.sleep(count*1000);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<int[]>() {
                    @Override
                    public void onNext(int[] responce) {
//                        Log.d(TAG, "onNext: "+responce[0]+"  "+responce[1]);
                        mPostText.setText(responce[1]>1024?String.format("%.1fkb/s",responce[1]/1024f)
                                :responce[1]+"b/s");
                        mGetText.setText(responce[0]>1024?String.format("%.1fkb/s",responce[0]/1024f)
                                :responce[0]+"b/s");
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: 网速停测");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }



}
